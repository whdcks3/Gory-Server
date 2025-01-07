package com.whdcks3.portfolio.gory_server.data.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.enums.Gender;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SquadDetailDto {
    private String status;
    private String category;
    private String title;
    private String notice;
    private String datetime;
    private String region;
    private String genderRequirement;
    private String ageRequirement;
    private int maxParticipantCount;
    private boolean isOwner;
    private boolean hasPendingUsers;
    private List<UserSimpleDto> participants;

    public static SquadDetailDto toDto(User user, Squad squad) {
        String status = calcStatus(squad);
        String category = squad.getCategory();
        String title = squad.getTitle();
        String notice = squad.getNotice();
        String datetime = formatDatetime(squad);
        String region;
        String genderRequirement;
        String ageRequirement;
        int maxParticipantCount;
        boolean isOwner;
        boolean hasPendingUsers;
        List<UserSimpleDto> participants;

        return toDto(user, squad);
    }

    private static String calcStatus(Squad squad) {
        if (squad.isClosed() || (squad.getDate().isBefore(LocalDate.now()) || squad.getDate().isEqual(LocalDate.now())
                && squad.getTime() != null && squad.getTime().isBefore(LocalTime.now()))) {
            return "모집 마감";
        }
        return "모집 중";
    }

    private static String formatDatetime(Squad squad) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        String dateStr = squad.getDate().format(dateFormatter);
        if (squad.getTime() == null) {
            return dateStr;
        }
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateStr + " " + squad.getTime().format(timeFormatter);
    }
}
