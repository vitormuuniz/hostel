package br.com.hostel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.hostel.models.DailyRate;

public interface DailyRateRepository extends JpaRepository<DailyRate, Long> {

}
