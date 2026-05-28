package com.kgstrivers.trainticker.Controllers;

import com.kgstrivers.trainticker.DAO.BookingRequest;
import com.kgstrivers.trainticker.DAO.BookingResponse;
import com.kgstrivers.trainticker.Entities.Booking;
import com.kgstrivers.trainticker.Services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> bookTicket(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.bookTicket(request));
    }

    @GetMapping("/{pnr}")
    public ResponseEntity<BookingResponse> getBookingByPnr(@PathVariable String pnr) {
        return ResponseEntity.ok(bookingService.getBookingByPnr(pnr));
    }
}
