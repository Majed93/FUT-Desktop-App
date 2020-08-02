package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonFormat
public class Pack {
    private String actionType;

    private long assetId;

    private long coins;

    private List<Currency> currencies;

    private String customAttribute;

    private String dealType;

    private String description;

    private PackDisplayGroup displayGroup;

    private String displayGroupAssetId;

    private Boolean displayGroupUseDefaultImage;

    private PackExt extPrice;

    private long fifaCashPrice;

    private String firstPartyStoreId;

    private long id;

    private Boolean isPremium;

    private Boolean isSeasonTicketDiscount;

    private String lastPurchasedTime;

    private PackContentInfo packContentInfo;

    private String packType;

    private String productId;

    private long purchaseCount;

    private long purchaseLimit;

    private String purchaseMethod;

    private int quantity;

    private long saleId;

    private String saleType;

    private long sortPriority;

    private long secondsUntilStart;

    private long secondsUntilEnd;

    private long start;

    private long end;

    private String state;

    private String type;

    private Boolean unopened;

    private Boolean useDefaultImage;
}
