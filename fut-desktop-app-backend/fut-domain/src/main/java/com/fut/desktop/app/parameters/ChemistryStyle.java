package com.fut.desktop.app.parameters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonFormat
@AllArgsConstructor
public enum ChemistryStyle {
    All(0),
    Basic(250),
    Sniper(251),
    Finisher(252),
    Deadeye(253),
    Marksman(254),
    Hawk(255),
    Artist(256),
    Architect(257),
    Powerhouse(258),
    Maestro(259),
    Engine(260),
    Sentinel(261),
    Guardian(262),
    Gladiator(263),
    Backbone(264),
    Anchor(265),
    Hunter(266),
    Catalyst(267),
    Shadow(268),
    Wall(269),
    Shield(270),
    Cat(271),
    Glove(272),
    GkBasic(273);

    private int chemistryStyle;

    @JsonCreator
    public static ChemistryStyle fromValue(int chemistryStyle) {
        for (ChemistryStyle c : values()) {
            if (chemistryStyle == c.chemistryStyle) {
                return c;
            }
        }

        return ChemistryStyle.Basic;
    }

    @JsonValue
    public int getChemistryStyle() {
        return chemistryStyle;
    }
}
