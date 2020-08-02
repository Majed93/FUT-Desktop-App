package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class SquadPlayer {
    private int index;

    private ItemData itemData;

    private int kitNumber;

    private int loyaltyBonus;
}
