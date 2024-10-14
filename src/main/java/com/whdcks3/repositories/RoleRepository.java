package com.whdcks3.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whdcks3.data.models.Role;
import com.whdcks3.enums.ERole;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

}
