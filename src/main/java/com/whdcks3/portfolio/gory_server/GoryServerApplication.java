package com.whdcks3.portfolio.gory_server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.whdcks3.portfolio.gory_server.data.models.Role;
import com.whdcks3.portfolio.gory_server.enums.ERole;
import com.whdcks3.portfolio.gory_server.repositories.RoleRepository;

@SpringBootApplication
public class GoryServerApplication {
	@Autowired
	private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(GoryServerApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void init() {
		if (!roleRepository.findByName(ERole.ROLE_USER).isPresent()) {
			roleRepository.save(new Role(ERole.ROLE_USER));
		}
	}
}
