package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonFormat
public class SquadDetailsResponse {
    private List<ItemData> actives;

    private long captain; //seems to be resource ID

    private int changed;

    private int chemistry;

    private String formation;

    private int Id;

    private List<Kicktaker> kicktakers;

    private List<ItemData> manager;

    private int newsquad;

    private long personaId;

    private List<SquadPlayer> players;

    private int rating;

    private String squadName;

    private int starRating;

    private Boolean valid;

    private Boolean dreamSquad;

    private String custom;

    private String squadType;
}
