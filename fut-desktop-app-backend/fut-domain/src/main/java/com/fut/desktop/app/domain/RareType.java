package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonFormat
@AllArgsConstructor
public enum RareType {
    Regular(0),
    Shiny(1),
    InForm(3),
    TeamOfTheYear(5),
    TeamOfTheYear2(6),
    ManOfTheMatch(8),
    TeamOfTheSeason(11);

    private int rareType;

    @JsonCreator
    public static RareType fromValue(int rareType) {
        for (RareType r : values()) {
            if (rareType == r.rareType) {
                return r;
            }
        }
        return RareType.Regular;
    }

    @JsonValue
    public int getRareType() {
        return rareType;
    }

}
