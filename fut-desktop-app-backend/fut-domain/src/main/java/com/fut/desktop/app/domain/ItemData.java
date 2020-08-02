package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.parameters.ChemistryStyle;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@JsonFormat
@ToString
public class ItemData {
    private long assetId;

    private int assists;

    private List<Attribute> attributeList;

    private int cardSubTypeId;

    private byte contract;

    private Boolean dream;

    private long discardValue;

    private byte fitness;

    private String formation;

    private long id;

    private byte injuryGames;

    private String injuryType;

    private String itemState;

    private String itemType;

    private long lastSalePrice;

    private long leagueId;

    private int lifeTimeAssists;

    private int loans;

    private List<Attribute> lifeTimeStats;

    private byte loyaltyBonus;

    private byte morale;

    private byte owners;

    private ChemistryStyle playStyle;

    private String preferredPosition;

    private byte rareFlag;

    private byte rating;

    private long resourceId;

    private List<Attribute> statsList;

    private byte suspension;

    private long teamId;

    private String timestamp;

    private int training;

    private Boolean untradeable;

    private int pile;

    private int nation;

    private long cardAssetId;

    private long value;

    private String name;

    private String category;

    private int marketDataMaxPrice;

    private int marketDataMinPrice;

    private int weightRare;

    private int bronze;

    private int silver;

    private int gold;

    private int amount;

    private String firstName;

    private String lastName;

    private String negotiation;

    private String bioDescription;

    private long stadiumId;

    private int capacity;

    private String header;

    private int year;

    private String manufacturer;

    private String description;

    private String internationalRep;

    private String attribute;

    private String fieldpos;

    private String posBonus;

    private ItemData trainingItem;

    private int resourceGameYear;

    private String guidAssetId;

    private Boolean reason;

    private Integer chantsCount;

    private List<Integer> groups;
}
