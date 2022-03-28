package br.com.hostel.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.hostel.models.Room;

public interface RoomRepository extends JpaRepository<Room, Long>{

	Optional<Room> findByNumber(Integer number);
}