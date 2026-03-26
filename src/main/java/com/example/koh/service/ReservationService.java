package com.example.koh.service;

import com.example.koh.entity.Reservation;
import com.example.koh.form.ReservationForm;
import com.example.koh.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Reservation createReservation(ReservationForm form) {
        Reservation reservation = new Reservation();
        reservation.setReservationDate(form.getReservationDate());
        reservation.setReservationTime(form.getReservationTime());
        reservation.setPartySize(form.getPartySize());
        reservation.setName(form.getName());
        reservation.setEmail(form.getEmail());
        reservation.setPhone(form.getPhone());
        reservation.setNote(form.getNote());
        return reservationRepository.save(reservation);
    }
}
