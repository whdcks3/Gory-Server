package com.whdcks3.portfolio.gory_server.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.whdcks3.portfolio.gory_server.data.dto.SquadChatDto;
import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadChat;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadChatImage;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.repositories.SquadChatRepository;
import com.whdcks3.portfolio.gory_server.repositories.SquadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SquadChatService {
        private final SquadChatRepository squadChatRepository;
        private final SquadRepository squadRepository;

        @Transactional
        public void saveMessage(User user, SquadChatDto chatDto) {
                Squad squad = squadRepository.findById(chatDto.getSquadId())
                                .orElseThrow(() -> new IllegalArgumentException());

                List<SquadChatImage> images = chatDto.getImages().stream()
                                .map(name -> new SquadChatImage(name)).collect(Collectors.toList());

                SquadChat chatMessage = SquadChat.builder()
                                .user(user)
                                .squad(squad)
                                .message(chatDto.getMessage())
                                .type(chatDto.getType())
                                .images(images)
                                .imageCount(images.size())
                                .build();
                images.forEach(image -> image.initSquadChat(chatMessage));

                squadChatRepository.save(chatMessage);
        }

        // public List<SquadChatDto> getMessageBySquad(User user, Long squadId) {
        // List<SquadChat> messages =
        // squadChatRepository.findBySquadChatIdOrderByCreatedAtAsc(squadId);

        // return messages.stream().map(msg -> SquadChatDto.toDto(msg,
        // user)).collect(Collectors.toList());
        // }
        // te
}
