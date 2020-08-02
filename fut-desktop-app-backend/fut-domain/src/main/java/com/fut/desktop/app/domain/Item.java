package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class Item {
    /**
     * Players - Defender
     * ------------------
     * Attribute1 = Pace
     * Attribute2 = Shooting
     * Attribute3 = Passing
     * Attribute4 = Dribbling
     * Attribute5 = Defending
     * Attribute6 = Heading
     */

    private byte attribute1;

    private byte attribute2;

    private byte attribute3;

    private byte attribute4;

    private byte attribute5;

    private byte attribute6;

    private long clubId;

    private String commonName;

    private DateOfBirth dateOfBirth;

    private String firstName;

    private byte height;

    private String itemType;

    private String lastName;

    private long leagueId;

    private long nationId;

    private String preferredFoot;

    private RareType rare;

    private byte rating;


    public byte getRating() {
        return rating;
    }

    public void setRating(byte value) {
        rating = value;
        SetCardType();
    }

    public CardType CardType;

    private void SetCardType() {
        if (rating < 65) {
            CardType = com.fut.desktop.app.domain.CardType.Bronze;
        } else if (rating < 75) {
            CardType = com.fut.desktop.app.domain.CardType.Silver;
        }
    }

    public String desc;

    public long bronze;
    public long silver;
    public long gold;

    public long value;

    public long weight;
    public long formationId;
    public long talkRating;
    public long negotiation;
    public long assetId;
    public long attr;
    public long amount;

    public long assetYear;
    public long category;
    public String bioDesc;

    public String manufacturer;
    public String name;

    public long stadiumId;
    public long cap; //Capacity
    public long boost;

    public long pos;
    public long posBonus;
    public String internationalRep;
}
