package br.com.hostel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.hostel.models.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
