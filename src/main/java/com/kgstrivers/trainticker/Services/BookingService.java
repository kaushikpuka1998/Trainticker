package com.kgstrivers.trainticker.Services;

import com.kgstrivers.trainticker.DAO.BookingRequest;
import com.kgstrivers.trainticker.DAO.BookingResponse;
import com.kgstrivers.trainticker.DAO.PassengerRequest;
import com.kgstrivers.trainticker.DAO.PassengerSeatResponse;
import com.kgstrivers.trainticker.Entities.*;
import com.kgstrivers.trainticker.Enums.BookingStatus;
import com.kgstrivers.trainticker.Enums.Gender;
import com.kgstrivers.trainticker.Enums.SeatStatus;
import com.kgstrivers.trainticker.Exception.InvalidBookingException;
import com.kgstrivers.trainticker.Exception.ResourceNotFoundException;
import com.kgstrivers.trainticker.Repositories.*;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    @Autowired
    private final TrainRepository trainRepository;
    @Autowired
    private final RouteStationRepository routeStationRepository;
    @Autowired
    private final SeatRepository seatRepository;
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final StationRepository stationRepository;
    @Autowired
    private final CoachTypeConfigRepository coachTypeConfigRepository;
    @Autowired
    private final BookedSeatRepository bookedSeatRepository;

    public BookingResponse bookTicket(BookingRequest request) {
        Train train = trainRepository.findByTrainNumber(request.getTrainNumber()).orElseThrow(() -> new ResourceNotFoundException("Train", request.getTrainNumber()));
        Station source = stationRepository.findByCode(request.getSourceStationCode()).orElseThrow(() -> new ResourceNotFoundException("Source station", request.getSourceStationCode()));
        Station destination = stationRepository.findByCode(request.getDestinationStationCode()).orElseThrow(() -> new ResourceNotFoundException("Destination station", request.getDestinationStationCode()));
        RouteStation sourceRoute = routeStationRepository
                .findByTrain_IdAndStation_Code(train.getId(), source.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("Route for source", source.getCode()));
        RouteStation destinationRoute = routeStationRepository.findByTrain_IdAndStation_Code(train.getId(), destination.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("Route for destination", destination.getCode()));


        if(request.getJourneyDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Please Provide future Journey Date");
        }
        if (sourceRoute.getStationOrder() >= destinationRoute.getStationOrder()) {
            throw new RuntimeException("Invalid route");
        }

        if (request.getJourneyDate().isBefore(LocalDate.now())) {
            throw new InvalidBookingException("Journey date must be in the future");
        }

        if (sourceRoute.getStationOrder() >= destinationRoute.getStationOrder()) {
            throw new InvalidBookingException("Invalid route: source must come before destination");
        }

        if (Objects.equals(request.getClassType(), "PANTRY")) {
            throw new InvalidBookingException("Pantry coaches are not bookable");
        }

        List<Seat> allSeats = seatRepository.findByCoach_Train_IdAndCoach_CoachType(train.getId(), request.getClassType());
        List<Booking> existingBookings = bookingRepository.findByTrain_IdAndJourneyDate(train.getId(), request.getJourneyDate());
        Set<Integer> bookedSeatIds = existingBookings.stream().flatMap(booking -> booking.getBookedSeats().stream()).filter(bookedSeat -> bookedSeat.getSeat() != null).map(bookedSeat -> bookedSeat.getSeat().getId()).collect(Collectors.toSet());
        List<Seat> availableSeats = allSeats.stream().filter(seat -> !bookedSeatIds.contains(seat.getId())).toList();

        if (Objects.equals(request.getClassType(), "PANTRY")) {
            throw new IllegalArgumentException("Pantry coaches are not bookable");
        }
        CoachTypeConfig config =
                coachTypeConfigRepository
                        .findByCoachType(request.getClassType())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Coach Type not found: " + request.getClassType()
                        ));

        long confirmedBookedCount =
                existingBookings.stream()
                        .flatMap(
                                booking ->
                                        booking.getBookedSeats().stream()
                        )
                        .filter(
                                bookedSeat ->
                                        bookedSeat.getSeatStatus()
                                                == SeatStatus.CONFIRMED
                        )
                        .count();


        long existingRacCount =
                existingBookings.stream()
                        .flatMap(
                                booking ->
                                        booking.getBookedSeats().stream()
                        )
                        .filter(
                                bookedSeat ->
                                        bookedSeat.getSeatStatus()
                                                == SeatStatus.RAC
                        )
                        .count();

        long existingWaitingCount =
                existingBookings.stream()
                        .flatMap(
                                booking ->
                                        booking.getBookedSeats().stream()
                        )
                        .filter(
                                bookedSeat ->
                                        bookedSeat.getSeatStatus()
                                                == SeatStatus.WAITLIST
                        )
                        .count();
        Booking booking = new Booking();
        booking.setTrain(train);
        booking.setJourneyDate(request.getJourneyDate());
        booking.setSourceStation(source);
        booking.setDestinationStation(destination);
        booking.setPnr(generatePNR());
        Long mask = generateMask(
                sourceRoute.getStationOrder(),
                destinationRoute.getStationOrder()
        );
        booking.setJourneyMask(mask);

        List<Passenger> passengers = new ArrayList<>();
        List<BookedSeat> bookedSeats = new ArrayList<>();
        int seatIndex = 0;
        for (int i = 0; i < request.getPassengers().size(); i++) {
            PassengerRequest passengerRequest =
                    request.getPassengers().get(i);

            Seat seat = null;
            SeatStatus seatStatus;
            Integer racNumber = null;
            Integer waitingNumber = null;
            if (confirmedBookedCount < config.getConfirmedCapacity()) {
                seat = availableSeats.get(seatIndex++);
                seatStatus = SeatStatus.CONFIRMED;
                confirmedBookedCount++;
            } else if (existingRacCount < config.getRacCapacity()) {
                seatStatus = SeatStatus.RAC;
                racNumber = (int) existingRacCount + 1;
                existingRacCount++;
            } else if (existingWaitingCount < config.getWaitingLimit()) {
                seatStatus = SeatStatus.WAITLIST;
                waitingNumber = (int) existingWaitingCount + 1;
                existingWaitingCount++;
            } else {
                throw new RuntimeException("No seats available");
            }

            Passenger passenger = Passenger.builder()
                    .name(passengerRequest.getName())
                    .age(passengerRequest.getAge())
                    .gender(Gender.valueOf(passengerRequest.getGender()))
                    .booking(booking)
                    .build();
            passengers.add(passenger);

            BookedSeat bookedSeat = BookedSeat.builder()
                    .passengerName(passenger.getName())
                    .passengerAge(passenger.getAge())
                    .gender(passenger.getGender().name())
                    .booking(booking)
                    .racNumber(racNumber)
                    .waitingNumber(waitingNumber)
                    .passenger(passenger)
                    .seat(seat)
                    .seatStatus(seatStatus)
                    .build();
            bookedSeats.add(bookedSeat);
            booking.setStatus(seatStatus == SeatStatus.CONFIRMED ? BookingStatus.CONFIRMED : (seatStatus == SeatStatus.RAC ? BookingStatus.RAC : BookingStatus.WAITLIST));
        }
        booking.setPassengers(passengers);
        booking.setBookedSeats(bookedSeats);
        bookingRepository.save(booking);

        List<PassengerSeatResponse> passengerResponses = new ArrayList<>();
        for (BookedSeat bs : bookedSeats) {
            Passenger p = bs.getPassenger();
            passengerResponses.add(PassengerSeatResponse.builder()
                    .id(p.getId())
                    .passengerName(bs.getPassengerName())
                    .coachNumber(bs.getSeat() != null ? bs.getSeat().getCoach().getCoachNumber() : null)
                    .seatNumber(bs.getSeat() != null
                            ? bs.getSeat().getSeatNumber()
                            : (bs.getRacNumber() != null ? "RAC-" + bs.getRacNumber() : "WL-" + bs.getWaitingNumber()))
                    .berthType(bs.getSeat() != null ? bs.getSeat().getBerthType() : null)
                    .bookingStatus(String.valueOf(bs.getSeatStatus()))
                    .build());
        }
        return BookingResponse.builder().pnr(booking.getPnr()).trainNumber(train.getTrainNumber()).trainName(train.getTrainName()).journeyDate(request.getJourneyDate()).source(source.getCode()).destination(destination.getCode()).bookingStatus(String.valueOf(booking.getStatus())).passengers(passengerResponses).build();
    }

    private String generatePNR() {
        return String.valueOf(ThreadLocalRandom.current().nextLong(1000000000L, 9999999999L));
    }

    private Long generateMask(
            int sourceOrder,
            int destinationOrder
    ) {

        long mask = 0;

        for (int i = sourceOrder;
             i < destinationOrder;
             i++) {

            mask |= (1L << i);
        }

        return mask;
    }

    @Cacheable(
            value = "pnrCache",
            key = "#pnr"
    )
    public BookingResponse getBookingByPnr(String pnr) {
            return bookingRepository.findByPnr(pnr)
                    .map(booking -> {

                        List<PassengerSeatResponse> passengerResponses =
                                booking.getBookedSeats()
                                        .stream()
                                        .map(bookedSeat -> {

                                            String coachNumber = null;
                                            String seatNumber = null;

                                            if (bookedSeat.getSeat() != null) {

                                                coachNumber =
                                                        bookedSeat.getSeat()
                                                                .getCoach()
                                                                .getCoachNumber();

                                                seatNumber =
                                                        bookedSeat.getSeat()
                                                                .getSeatNumber();

                                            } else {

                                                if (bookedSeat.getSeatStatus()
                                                        == SeatStatus.RAC) {

                                                    seatNumber =
                                                            "RAC-"
                                                                    + bookedSeat
                                                                    .getRacNumber();

                                                } else if (
                                                        bookedSeat.getSeatStatus()
                                                                == SeatStatus.WAITLIST
                                                ) {

                                                    seatNumber =
                                                            "WL-"
                                                                    + bookedSeat
                                                                    .getWaitingNumber();
                                                }
                                            }

                                            BerthType berthType = null;
                                            if (bookedSeat.getSeat() != null) {
                                                berthType = bookedSeat.getSeat().getBerthType();
                                            }

                                            return PassengerSeatResponse
                                                    .builder()

                                                    .passengerName(
                                                            bookedSeat.getPassengerName()
                                                    )
                                                    .id(
                                                            bookedSeat.getPassenger().getId()
                                                    )

                                                    .coachNumber(
                                                            coachNumber
                                                    )

                                                    .seatNumber(
                                                            seatNumber
                                                    )

                                                    .bookingStatus(
                                                            String.valueOf(
                                                                    bookedSeat.getSeatStatus()
                                                            )
                                                    )

                                                    .berthType(berthType)

                                                    .build();
                                        })
                                        .toList();

                        return BookingResponse.builder()
                                .pnr(booking.getPnr())
                                .trainNumber(booking.getTrain().getTrainNumber())
                                .trainName(booking.getTrain().getTrainName())
                                .journeyDate(booking.getJourneyDate())
                                .source(booking.getSourceStation().getCode())
                                .destination(booking.getDestinationStation().getCode())
                                .bookingStatus(String.valueOf(booking.getStatus()))
                                .passengers(passengerResponses)
                                .build();
                    })
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @CacheEvict(
            value = "pnrCache",
            key = "#pnr"
    )
    @Transactional
    public void cancelTicket(
            String pnr,
            Long passengerId
    ) {

        Booking booking = bookingRepository.findByPnr(pnr).orElseThrow(() -> new RuntimeException("Booking not found"));

        // FULL CANCELLATION

        if (passengerId == null) {
            List<BookedSeat> confirmedSeats = booking.getBookedSeats().stream().toList();

            for (BookedSeat cancelledSeat
                    : confirmedSeats) {
                Seat releasedSeat = cancelledSeat.getSeat();
                cancelledSeat.setSeatStatus(SeatStatus.CANCELLED);
                cancelledSeat.setSeat(null);
            }

            booking.setStatus(BookingStatus.CANCELLED);
            promoteRacPassenger(booking.getTrain().getId(), booking.getJourneyDate(), null);
        }

        // PARTIAL CANCELLATION

        else {

            BookedSeat cancelledSeat = booking.getBookedSeats().stream().filter(bookedSeat -> bookedSeat.getPassenger().getId()
                                                    .equals(passengerId))
                                                    .findFirst()
                                                    .orElseThrow(() -> new RuntimeException("Passenger not found"));

            SeatStatus oldStatus = cancelledSeat.getSeatStatus();
            if (oldStatus == SeatStatus.CONFIRMED) {
                Seat releasedSeat = cancelledSeat.getSeat();
                cancelledSeat.setSeatStatus(SeatStatus.CANCELLED);
                cancelledSeat.setSeat(null);
                promoteRacPassenger(booking.getTrain().getId(), booking.getJourneyDate(), releasedSeat);
            } else {
                cancelledSeat.setSeatStatus(SeatStatus.CANCELLED);
                if (oldStatus == SeatStatus.RAC) {
                    reorderRacList(booking.getTrain().getId(), booking.getJourneyDate());
                } else if (oldStatus == SeatStatus.WAITLIST) {
                    reorderWaitingList(booking.getTrain().getId(), booking.getJourneyDate());
                }
            }
            // Check if all passengers canceled
            boolean allCancelled = booking.getBookedSeats().stream().allMatch(bookedSeat -> bookedSeat.getSeatStatus() == SeatStatus.CANCELLED);

            if (allCancelled) {
                booking.setStatus(BookingStatus.CANCELLED);
            }
        }

        bookingRepository.save(booking);
    }

    private void promoteRacPassenger(Long trainId, LocalDate journeyDate, Seat releasedSeat) {
        List<BookedSeat> racPassengers = bookedSeatRepository.findByBooking_Train_IdAndBooking_JourneyDateAndSeatStatusOrderByIdAsc(trainId, journeyDate, SeatStatus.RAC);
        if (racPassengers.isEmpty()) {
            return;
        }
        BookedSeat racPassenger = racPassengers.get(0);
        racPassenger.setSeat(releasedSeat);
        racPassenger.setSeatStatus(SeatStatus.CONFIRMED);
        racPassenger.getBooking().setStatus(BookingStatus.CONFIRMED);
        racPassenger.setRacNumber(null);
        bookedSeatRepository.save(racPassenger);
        promoteWaitingPassenger(trainId, journeyDate);
    }

    private void promoteWaitingPassenger(Long trainId, LocalDate journeyDate) {
        List<BookedSeat> waitingPassengers = bookedSeatRepository.findByBooking_Train_IdAndBooking_JourneyDateAndSeatStatusOrderByIdAsc(trainId, journeyDate, SeatStatus.WAITLIST);
        if (waitingPassengers.isEmpty()) {
            return;
        }
        BookedSeat waitingPassenger = waitingPassengers.get(0);
        waitingPassenger.setSeatStatus(SeatStatus.RAC);
        waitingPassenger.getBooking().setStatus(BookingStatus.RAC);
        waitingPassenger.setWaitingNumber(null);
        bookedSeatRepository.save(waitingPassenger);
        reorderWaitingList(trainId, journeyDate);
        reorderRacList(trainId, journeyDate);
    }

    private void reorderRacList(Long trainId, LocalDate journeyDate) {
        List<BookedSeat> racPassengers = bookedSeatRepository.findByBooking_Train_IdAndBooking_JourneyDateAndSeatStatusOrderByIdAsc(trainId, journeyDate, SeatStatus.RAC);
        for (int i = 0; i < racPassengers.size(); i++) {
            racPassengers.get(i).setRacNumber(i + 1);
        }
        bookedSeatRepository.saveAll(racPassengers);
    }

    private void reorderWaitingList(Long trainId, LocalDate journeyDate) {
        List<BookedSeat> waitingPassengers = bookedSeatRepository.findByBooking_Train_IdAndBooking_JourneyDateAndSeatStatusOrderByIdAsc(trainId, journeyDate, SeatStatus.WAITLIST);
        for (int i = 0; i < waitingPassengers.size(); i++) {
            waitingPassengers.get(i).setWaitingNumber(i + 1);
        }
        bookedSeatRepository.saveAll(waitingPassengers);
    }


    public void cancelPassengerFromBooking(String pnr, Long passengerId) {
        Booking booking = bookingRepository.findByPnr(pnr)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        boolean allCancelled =
                booking.getPassengers()
                        .stream()
                        .allMatch(
                                passenger ->
                                        passenger.getStatus()
                                                == BookingStatus.CANCELLED
                        );

        if (allCancelled) {
            booking.setStatus(BookingStatus.CANCELLED);
        }
    }
}
