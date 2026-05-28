package com.kgstrivers.trainticker.Controllers;

import com.kgstrivers.trainticker.Entities.Booking;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class BookingController {

    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return new ArrayList<>();
    }
}
