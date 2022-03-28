package br.com.hostel.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.hostel.repository.GuestRepository;

@Service
public class AutenticationService implements UserDetailsService {

	private final GuestRepository repository;

	@Autowired
	public AutenticationService(GuestRepository repository) {
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		return repository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Ivalid data"));
	}
}
