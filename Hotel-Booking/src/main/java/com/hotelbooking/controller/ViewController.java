package com.hotelbooking.controller;

import com.hotelbooking.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ViewController {
    private final BillingService billingService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/hotels")
    public String hotels() {
        return "hotels";
    }

    @GetMapping("/hotels/{hotelId}")
    public String hotelDetails(@PathVariable Long hotelId, Model model) {
        model.addAttribute("hotelId", hotelId);
        return "hotel-details";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/my-bookings")
    public String myBookings() {
        return "my-bookings";
    }

    @GetMapping("/booking-confirmation")
    public String bookingConfirmation() {
        return "booking-confirmation";
    }

    @GetMapping("/invoice/{bookingId}")
    public String invoice(@PathVariable Long bookingId, Model model) {
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("invoice", billingService.getBillForBooking(bookingId));
        return "invoice";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/admin/hotels/new")
    public String addHotel() {
        return "hotel-form";
    }

    @GetMapping("/admin/hotels/{hotelId}/edit")
    public String editHotel(@PathVariable Long hotelId, Model model) {
        model.addAttribute("hotelId", hotelId);
        return "hotel-form";
    }

    @GetMapping("/admin/hotels/{hotelId}/rooms/new")
    public String addRoom(@PathVariable Long hotelId, Model model) {
        model.addAttribute("hotelId", hotelId);
        return "room-form";
    }

    @GetMapping("/admin/rooms/{roomId}/edit")
    public String editRoom(@PathVariable Long roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "room-form";
    }
}
