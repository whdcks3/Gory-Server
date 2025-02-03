package com.whdcks3.portfolio.gory_server.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.whdcks3.portfolio.gory_server.common.Utils;
import com.whdcks3.portfolio.gory_server.data.dto.SquadDetailDto;
import com.whdcks3.portfolio.gory_server.data.dto.SquadFilterRequest;
import com.whdcks3.portfolio.gory_server.data.dto.SquadSimpleDto;
import com.whdcks3.portfolio.gory_server.data.models.Block;
import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant.SquadParticipationStatus;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;
import com.whdcks3.portfolio.gory_server.data.responses.DataResponse;
import com.whdcks3.portfolio.gory_server.enums.Gender;
import com.whdcks3.portfolio.gory_server.enums.JoinType;
import com.whdcks3.portfolio.gory_server.exception.MemberNotEqualsException;
import com.whdcks3.portfolio.gory_server.firebase.FirebasePublisherUtil;
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

    private final FirebasePublisherUtil firebasePublisherUtil;

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
        if (squad.getParticipants().stream()
                .anyMatch(paritipant -> Utils.calculateAge(paritipant.getUser().getBirth()) < req.getMinAge())
                || squad.getParticipants().stream().anyMatch(
                        paritipant -> Utils.calculateAge(paritipant.getUser().getBirth()) > req.getMaxAge())) {
            throw new IllegalArgumentException("이미 참여 중인 다른 나이대의 멤버가 있어서 수정이 어려워요.");
        }

        if (squad.getParticipants().stream().anyMatch(paritipant -> paritipant.getUser().getGender().equals("남성")
                && req.getGenderRequirement() == Gender.FEMALE
                || paritipant.getUser().getGender().equals("여성") && req.getGenderRequirement() == Gender.MALE)) {
            throw new IllegalArgumentException("이미 참여 중인 다른 성별의 멤버가 있어서 수정이 어려워요.");
        }

        int joinedCount = (int) squad.getParticipants().stream()
                .filter(paritipant -> paritipant.getStatus() == SquadParticipationStatus.JOINED)
                .count();
        if (joinedCount < req.getMaxParticipants()) {
            throw new IllegalArgumentException(String.format("이미 %d명이 참여중이라 인원 수정이 어려워요.", joinedCount));
        }
        squad.update(req);
    }

    @Transactional
    public void deleteSquad(User user, Long sid, boolean isForcedDelete) {
        Squad squad = squadRepository.findById(sid).orElseThrow();
        validateOwner(user, squad);

        if (!isForcedDelete && !squad.isOnlyOneLeft()) {
            throw new IllegalArgumentException("멤버를 모두 내보낸 다음 삭제할 수 있어요. 멤버 닉네임 옆의 '관리'를 눌러주세요");
        }
        // while (squad.getParticipants().size() > 0) {
        // squadParticipantRepository.delete(squad.getParticipants().get(0));
        // }
        List<SquadParticipant> participants = squad.getParticipants();
        squad.getParticipants().clear();
        squadParticipantRepository.deleteAll(participants);
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
        Squad squad = squadRepository.findById(squadPid)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임을 찾을 수 없습니다."));
        return SquadDetailDto.toDto(user, squad);
    }

    // 참여하기
    public void joinSquad(User user, Long sqaudId) {
        Squad squad = squadRepository.findById(sqaudId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임을 찾을 수 없습니다."));
        if (squad.getParticipants().stream().anyMatch(participant -> participant.getUser() == user)) {
            throw new IllegalArgumentException("이미 참여 중인 사용자입니다.");
        }
        if (squad.isClosed()) {
            throw new IllegalArgumentException("마감된 모임입니다.");
        }
        if (LocalDateTime.of(squad.getDate(), squad.getTime() == null ? LocalTime.of(23, 59, 59) : squad.getTime())
                .isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("종료된 모임입니다.");
        }
        if (squad.getParticipants().stream()
                .filter(participant -> participant.getStatus() == SquadParticipationStatus.JOINED)
                .count() >= squad.getMaxParticipants()) {
            throw new IllegalArgumentException("참여 인원이 찼습니다.");
        }
        if (squad.getGenderRequirement() == Gender.MALE && user.getGender().equals("여자")
                || squad.getGenderRequirement() == Gender.FEMALE && user.getGender().equals("남자")) {
            throw new IllegalArgumentException("참여 가능한 성별이 아닙니다.");
        }
        int age = Utils.calculateAge(user.getBirth());
        if (squad.getMinAge() > age || squad.getMaxAge() < age) {
            throw new IllegalArgumentException("참여 가능한 연령이 아닙니다.");
        }

        SquadParticipant squadParticipant = new SquadParticipant(user, squad);
        squadParticipant.setStatus(
                squad.getJoinType() == JoinType.APPROVAL ? SquadParticipationStatus.PENDING
                        : SquadParticipationStatus.JOINED);
        squadParticipant = squadParticipantRepository.save(squadParticipant);
        squad.getParticipants().add(squadParticipant);

        String fcmToken = squad.getUser().getFcmToken();
        boolean isAlarm = squad.getUser().getFeedLikeAlarm();
        String title = "모임에 새로운 멤버가 참여했습니다.";
        String content = String.format("회원님의 모임글 [%s]에 새로운 멤버가 참여했습니다.",
                squad.getTitle().substring(0, Math.max(20, squad.getTitle().length())));
        String data = squad.getPid() + ",squad";

        sendPushToClient(isAlarm, fcmToken, title, content, data);
    }

    // 승인하기
    public void approveParticipant(User user, Long userId, Long sqaudId) {
        Squad squad = squadRepository.findById(sqaudId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임을 찾을 수 없습니다."));
        User participantUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        if (squad.getUser() != user) {
            throw new IllegalArgumentException("방장이 아닙니다. 승인 권한이 없습니다.");
        }

        if (squad.getParticipants().stream()
                .filter(participant -> participant.getStatus() == SquadParticipationStatus.JOINED)
                .count() >= squad.getMaxParticipants()) {
            throw new IllegalArgumentException("참여 인원이 찼습니다.");
        }
        SquadParticipant participant = squad.getParticipants().stream().filter(p -> p.getUser() == participantUser)
                .findAny().orElseThrow();
        participant.setStatus(SquadParticipationStatus.JOINED);
        squadParticipantRepository.save(participant);
    }

    // 거절하기
    public void rejectParticipant(User user, Long userId, Long sqaudId) {
        Squad squad = squadRepository.findById(sqaudId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임을 찾을 수 없습니다."));
        User participantUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        if (squad.getUser() != user) {
            throw new IllegalArgumentException("방장이 아닙니다. 승인 권한이 없습니다.");
        }

        SquadParticipant participant = squad.getParticipants().stream().filter(p -> p.getUser() == participantUser)
                .findAny().orElseThrow();
        participant.setStatus(SquadParticipationStatus.REJECTED);
        squadParticipantRepository.save(participant);
    }

    // 내보내기
    public void kickOffParticipant(User user, Long userId, Long sqaudId) {
        Squad squad = squadRepository.findById(sqaudId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임을 찾을 수 없습니다."));
        User participantUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        if (squad.getUser() != user) {
            throw new IllegalArgumentException("방장이 아닙니다. 승인 권한이 없습니다.");
        }

        SquadParticipant participant = squad.getParticipants().stream().filter(p -> p.getUser() == participantUser)
                .findAny().orElseThrow();
        participant.setStatus(SquadParticipationStatus.KICKED_OUT);
        squad.decreaseCurrentCount();
        squadRepository.save(squad);
        squadParticipantRepository.save(participant);
    }

    // 모임에서 나오기
    @Transactional
    public void kickOffParticipant(User user, Long sqaudId) {
        Squad squad = squadRepository.findById(sqaudId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임을 찾을 수 없습니다."));

        if (squad.getUser() != user) {
            throw new IllegalArgumentException("방장이 아닙니다. 승인 권한이 없습니다.");
        }

        SquadParticipant participant = squad.getParticipants().stream().filter(p -> p.getUser() == user)
                .findAny().orElseThrow();
        squad.getParticipants().remove(participant);
        squadParticipantRepository.delete(participant);
    }

    @Transactional
    private void deleteFromParticipatedByUser(User user) {
        List<SquadParticipant> squadParticipants = squadParticipantRepository.findAllByUser(user);
        for (SquadParticipant squadParticipant : squadParticipants) {
            Squad squad = squadParticipant.getSquad();
            if (squadParticipant.getStatus() == SquadParticipationStatus.JOINED) {
                squad.decreaseCurrentCount();
            }
            squadRepository.save(squad);
            squadParticipantRepository.delete(squadParticipant);
        }
    }

    @Transactional
    private void deleteSquadsByUser(User user) {
        List<Squad> squads = squadRepository.findAllByUser(user);
        for (Squad squad : squads) {
            deleteSquad(user, squad.getPid(), true);
        }
    }

    @Transactional
    public void deleteByUser(User user) {
        deleteFromParticipatedByUser(user);
        deleteSquadsByUser(user);
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

    private String sendPushToClient(boolean isAlarm, String token, String title, String content, String data) {
        if (isAlarm && token != null) {
            try {
                return firebasePublisherUtil.postToClient(title, content, data, token);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
