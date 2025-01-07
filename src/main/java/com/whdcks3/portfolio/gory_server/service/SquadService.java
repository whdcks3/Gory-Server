package com.whdcks3.portfolio.gory_server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.whdcks3.portfolio.gory_server.data.dto.SquadFilterRequest;
import com.whdcks3.portfolio.gory_server.data.dto.SquadSimpleDto;
import com.whdcks3.portfolio.gory_server.data.dto.UserSimpleDto;
import com.whdcks3.portfolio.gory_server.data.models.Block;
import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;
import com.whdcks3.portfolio.gory_server.data.responses.DataResponse;
import com.whdcks3.portfolio.gory_server.exception.MemberNotEqualsException;
import com.whdcks3.portfolio.gory_server.repositories.BlockRespository;
import com.whdcks3.portfolio.gory_server.repositories.SquadParticipantRepository;
import com.whdcks3.portfolio.gory_server.repositories.SquadRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SquadService {

    private final SquadRepository squadRepository;

    private final SquadParticipantRepository squadParticipantRepository;

    private final UserRepository userRepository;

    private final BlockRespository blockRespository;

    public void createSquad(User user, SquadRequest req) {
        Squad squad = new Squad(user, req);
        squad = squadRepository.save(squad);

        SquadParticipant participant = new SquadParticipant(user, squad);
        squadParticipantRepository.save(participant);
    }

    public void modifySquad(Long uid, Long sid, SquadRequest req) {
        User user = userRepository.findById(uid).orElseThrow();
        Squad squad = squadRepository.findById(sid).orElseThrow();
        validateOwner(user, squad);
        squad.update(req);
    }

    @Transactional
    public void deleteSquad(Long uid, Long sid) {
        User user = userRepository.findById(uid).orElseThrow();
        Squad squad = squadRepository.findById(sid).orElseThrow();
        validateOwner(user, squad);
        squadRepository.delete(squad);
    }

    public void validateOwner(User user, Squad squad) {
        if (!user.equals(squad.getUser())) {
            throw new MemberNotEqualsException();
        }
    }

    public DataResponse mySquads(User user, Pageable pageable) {
        Page<Squad> squads = squadRepository.findByUser(user, pageable);
        List<SquadSimpleDto> squadDtos = squads.getContent().stream().map(SquadSimpleDto::toDto).toList();
        return new DataResponse(squads.hasNext(), squadDtos);
    }

    public DataResponse homeSquads(User user, SquadFilterRequest req, Pageable pageable) {
        List<User> excludedUsers = new ArrayList<>();
        if (user != null) {
            excludedUsers = getExcludedUsers(user);
        }
        Page<Squad> squads = squadRepository.findFilteredSquadsWithExclusion(req.getCategory(), req.getRegionMain(),
                req.getRegionSub(), req.isRecruitingOnly(), excludedUsers, pageable);
        return new DataResponse(squads.hasNext(), squads.getContent().stream().map(SquadSimpleDto::toDto).toList());
    }

    public SquadDetailDto detail(User user, long squadPid) {
        Squad squad = squadRepository.findById(squadPid).orElseThrow();
    }

    private List<User> getExcludedUsers(User currentUser) {
        List<User> blockedUsers = blockRespository.findByBlocker(currentUser).stream()
                .map(Block::getBlocked)
                .collect(Collectors.toList());
        List<User> blockedByUsers = blockRespository.findByBlocked(currentUser).stream()
                .map(Block::getBlocker)
                .collect(Collectors.toList());
        blockedUsers.addAll(blockedByUsers);
        return blockedUsers.stream().distinct().collect(Collectors.toList());
    }
}
