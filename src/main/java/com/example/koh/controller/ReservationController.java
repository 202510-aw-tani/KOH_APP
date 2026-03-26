package com.example.koh.controller;

import com.example.koh.form.ReservationForm;
import com.example.koh.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @ModelAttribute("reservationForm")
    public ReservationForm reservationForm() {
        return new ReservationForm();
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/reserve")
    public String reserve() {
        return "reserve";
    }

    @PostMapping("/reserve")
    public String submitReserve(@Valid @ModelAttribute("reservationForm") ReservationForm reservationForm,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "reserve";
        }
        reservationService.createReservation(reservationForm);
        return "redirect:/thanks";
    }

    @GetMapping("/thanks")
    public String thanks() {
        return "thanks";
    }
}
