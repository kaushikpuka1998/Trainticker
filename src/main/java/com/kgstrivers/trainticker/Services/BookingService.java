package com.kgstrivers.trainticker.Services;

import com.kgstrivers.trainticker.DAO.BookingRequest;
import com.kgstrivers.trainticker.DAO.BookingResponse;
import com.kgstrivers.trainticker.DAO.PassengerRequest;
import com.kgstrivers.trainticker.DAO.PassengerSeatResponse;
import com.kgstrivers.trainticker.Entities.*;
import com.kgstrivers.trainticker.Enums.BookingStatus;
import com.kgstrivers.trainticker.Enums.Gender;
import com.kgstrivers.trainticker.Enums.SeatStatus;
import com.kgstrivers.trainticker.Repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {
    private final TrainRepository trainRepository;
    private final RouteStationRepository routeStationRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final StationRepository stationRepository;
    private final CoachTypeConfigRepository coachTypeConfigRepository;

    public BookingResponse bookTicket(BookingRequest request) {
        Train train = trainRepository.findByTrainNumber(request.getTrainNumber()).orElseThrow(() -> new RuntimeException("Train not found"));
        Station source = stationRepository.findByCode(request.getSourceStationCode()).orElseThrow(() -> new RuntimeException("Source station not found"));
        Station destination = stationRepository.findByCode(request.getDestinationStationCode()).orElseThrow(() -> new RuntimeException("Destination station not found"));
        RouteStation sourceRoute = routeStationRepository.findByTrain_IdAndStation_Code(train.getId(), source.getCode()).orElseThrow();
        RouteStation destinationRoute = routeStationRepository.findByTrain_IdAndStation_Code(train.getId(), destination.getCode()).orElseThrow();
        if (sourceRoute.getStationOrder() >= destinationRoute.getStationOrder()) {
            throw new RuntimeException("Invalid route");
        }
        List<Seat> allSeats = seatRepository.findByCoach_Train_IdAndCoach_CoachType(train.getId(), request.getClassType());
        List<Booking> existingBookings = bookingRepository.findByTrain_IdAndJourneyDate(train.getId(), request.getJourneyDate());
        Set<Integer> bookedSeatIds = existingBookings.stream().flatMap(booking -> booking.getBookedSeats().stream()).map(bookedSeat -> bookedSeat.getSeat().getId()).collect(Collectors.toSet());
        List<Seat> availableSeats = allSeats.stream().filter(seat -> !bookedSeatIds.contains(seat.getId())).toList();

        CoachTypeConfig config =
                coachTypeConfigRepository
                        .findByCoachType(
                                request.getClassType()
                        )
                        .orElseThrow();

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

        List<PassengerSeatResponse> passengerResponses = new ArrayList<>();
        for (int i = 0; i < request.getPassengers().size(); i++) {
            PassengerRequest passengerRequest =
                    request.getPassengers().get(i);

            Seat seat = null;
            SeatStatus seatStatus;
            Integer racNumber = null;
            Integer waitingNumber = null;

            if (confirmedBookedCount < config.getConfirmedCapacity()) {
                seat = availableSeats.get((int) confirmedBookedCount);
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

            Passenger passenger = new Passenger();
            passenger.setName(passengerRequest.getName());
            passenger.setAge(passengerRequest.getAge());
            passenger.setGender(Gender.valueOf(passengerRequest.getGender()));
            passenger.setBooking(booking);
            passengers.add(passenger);
            passenger.setRacNumber(racNumber);

            passenger.setWaitingNumber(waitingNumber);

            BookedSeat bookedSeat = new BookedSeat();
            bookedSeat.setPassengerName(passenger.getName());
            bookedSeat.setPassengerAge(passenger.getAge());
            bookedSeat.setGender(passenger.getGender().name());
            bookedSeat.setBooking(booking);
            bookedSeat.setPassenger(passenger);
            if (seat != null) {
                bookedSeat.setSeat(seat);
            }
            bookedSeat.setSeatStatus(seatStatus);
            bookedSeats.add(bookedSeat);
            booking.setStatus(seatStatus == SeatStatus.CONFIRMED ? BookingStatus.CONFIRMED : (seatStatus == SeatStatus.RAC ? BookingStatus.RAC : BookingStatus.WAITLIST));

            passengerResponses.add(PassengerSeatResponse.builder()
                    .passengerName(passenger.getName())
                    .coachNumber(
                            seat != null
                                    ? seat.getCoach().getCoachNumber()
                                    : null
                    )

                    .seatNumber(
                            seat != null
                                    ? seat.getSeatNumber()
                                    : (
                                    racNumber != null
                                    ? "RAC-" + racNumber
                                    : "WL-" + waitingNumber
                            )
                    )
                    .bookingStatus(String.valueOf(seatStatus))
                    .build()
            );
        }

        booking.setBookedSeats(bookedSeats);
        bookingRepository.save(booking);
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
                                                                .getPassenger()
                                                                .getRacNumber();

                                            } else if (
                                                    bookedSeat.getSeatStatus()
                                                            == SeatStatus.WAITLIST
                                            ) {

                                                seatNumber =
                                                        "WL-"
                                                                + bookedSeat
                                                                .getPassenger()
                                                                .getWaitingNumber();
                                            }
                                        }

                                        return PassengerSeatResponse
                                                .builder()

                                                .passengerName(
                                                        bookedSeat.getPassengerName()
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

                                                .build();
                                    })
                                    .toList();

                    return BookingResponse.builder()

                            .pnr(
                                    booking.getPnr()
                            )

                            .trainNumber(
                                    booking.getTrain()
                                            .getTrainNumber()
                            )

                            .trainName(
                                    booking.getTrain()
                                            .getTrainName()
                            )

                            .journeyDate(
                                    booking.getJourneyDate()
                            )

                            .source(
                                    booking.getSourceStation()
                                            .getCode()
                            )

                            .destination(
                                    booking.getDestinationStation()
                                            .getCode()
                            )

                            .bookingStatus(
                                    String.valueOf(
                                            booking.getStatus()
                                    )
                            )

                            .passengers(
                                    passengerResponses
                            )

                            .build();
                })

                .orElseThrow(
                        () -> new RuntimeException(
                                "Booking not found"
                        )
                );
    }
}
