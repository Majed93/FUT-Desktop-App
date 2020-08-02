package com.fut.desktop.app.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonFormat
@AllArgsConstructor
public enum Pile {
    TradePile(2),
    UnknownPile3(3), // Size of this pile seems to be always -1
    WatchList(4),
    UnknownPile6(6), // Size of this pile seems to be always 15
    UnknownPile11(11), // This size seems to be 100
    UnassignedPileSize(99);
    private int pile;

    @JsonCreator
    public static Pile fromValue(int pile) {
        for (Pile p : values()) {
            if (pile == p.pile) {
                return p;
            }
        }

        return Pile.TradePile;
    }

    @JsonValue
    public int getPile() {
        return pile;
    }
}
