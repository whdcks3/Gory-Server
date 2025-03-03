package com.whdcks3.portfolio.gory_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whdcks3.portfolio.gory_server.data.dto.SquadDetailDto;
import com.whdcks3.portfolio.gory_server.data.dto.SquadFilterRequest;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;
import com.whdcks3.portfolio.gory_server.service.SquadService;

@RestController
@RequestMapping("/api/squad")
public class SquadRestController {

    @Autowired
    SquadService squadService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createSquad(@AuthenticationPrincipal User user, @RequestBody SquadRequest req) {
        squadService.createSquad(user, req);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/modify/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> modifySquad(@RequestParam Long id, @PathVariable Long sid,
            @RequestBody SquadRequest req) {
        squadService.modifySquad(id, sid, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteSquad(@AuthenticationPrincipal User user, @PathVariable("id") Long sid,
            @RequestParam(defaultValue = "false") boolean isForcedDelete) {
        squadService.deleteSquad(user, sid, isForcedDelete);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> mySquads(@AuthenticationPrincipal User user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        squadService.mySquads(user, pageable);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/home")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> homeSquads(@AuthenticationPrincipal User user, @ModelAttribute SquadFilterRequest req,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        squadService.homeSquads(user, req, pageable);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{sid}")
    public ResponseEntity<SquadDetailDto> detail(@AuthenticationPrincipal User user, @PathVariable Long sid) {
        SquadDetailDto response = squadService.detail(user, sid);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sid}/join")
    public ResponseEntity<?> approveSquad(@AuthenticationPrincipal User userId, @RequestParam Long sid) {
        squadService.joinSquad(userId, sid);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sid}/approve/{uid}")
    public ResponseEntity<?> approveParticipant(@AuthenticationPrincipal User user, @PathVariable Long sid,
            @PathVariable("uid") Long userId) {
        squadService.approveParticipant(user, userId, sid);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sid}/reject/{userId}")
    public ResponseEntity<?> rejectParticipant(@AuthenticationPrincipal User user, @PathVariable Long sid,
            @PathVariable Long userId) {
        squadService.rejectParticipant(user, userId, sid);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sid}/kick/{userId}")
    public ResponseEntity<?> kickOffParticipant(@AuthenticationPrincipal User user, @PathVariable Long sid,
            @PathVariable Long userId) {
        squadService.kickOffParticipant(user, userId, sid);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sid}/leave")
    public ResponseEntity<?> leaveSquad(@AuthenticationPrincipal User user, @PathVariable Long sid) {
        squadService.kickOffParticipant(user, sid);
        return ResponseEntity.ok().build();
    }

}
