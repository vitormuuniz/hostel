package br.com.hostel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.hostel.models.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>{
	
}
