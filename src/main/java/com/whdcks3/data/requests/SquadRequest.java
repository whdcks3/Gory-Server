package com.whdcks3.data.requests;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SquadRequest {
    private Long pid;
    private String name, category, regionMain, regionSub, ampm, age, gender, introduction;
    private Boolean isTimeSelected, shouldBeConfirmed;
    private Integer memberCount, startAge, endAge, hour, minute;
    private LocalDate date;
}
