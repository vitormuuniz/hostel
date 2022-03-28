package br.com.hostel.repositories;

import br.com.hostel.models.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long>{

	List<Guest> findByName(String nome);
	Optional<Guest> findByEmail(String email);
}
