package com.whdcks3.portfolio.gory_server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant;

public interface SquadParticipantRepository extends JpaRepository<SquadParticipant, Long> {

    // List<SquadParticipant> findBySquadParticipantId(Long squadParticipantId);

}
