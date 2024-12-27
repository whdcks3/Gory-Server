package com.whdcks3.portfolio.gory_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant;

public interface SquadParticipantRepository extends JpaRepository<SquadParticipant, Long> {

}
