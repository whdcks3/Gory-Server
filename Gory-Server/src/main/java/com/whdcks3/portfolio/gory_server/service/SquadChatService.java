package com.whdcks3.portfolio.gory_server.service;

import org.springframework.stereotype.Service;

@Service
public class SquadChatService {

    private final SquadChatRepository squadChatRepository;
    private final SquadRepository squadRepository;

    public SquadChatService(SquadChatRepository squadChatRepository, SquadRepository squadRepository) {
        this.squadChatRepository = squadRepository;
        this.squadChatRepository = squadChatRepository;
    }

    @Transactional
    public void saveMessage(SquadChatDTO chatDTO) {
        Squad squad = squadRepository.findById(chatDTO.getSquadId())
                .orElseThrow(() -> new IllegalArgumentException("Squad not found"));

        List<SquadChatImage> squadChatImages = chatDTO.getImageUrls().stream()
                .map(url -> new SquadChatImage(url, null))
                .collect(Collectors.toList());

        // 여기에 추가하는 것! (SquadChat에 대한 객체 생성) -> 변수 이름 : chatMessage

        squadChatImages.forEach(image -> image.setSquadChat(chatMessage));
        squadChatRepository.save(chatMessage);
    }
}
