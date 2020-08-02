package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonFormat
public class PurchasedPackResponse {
    private List<Long> awardSetIds;

    private List<DuplicateItem> duplicateItemIdList;

    private List<ItemData> itemData;

    private List<ItemData> itemList;

    private List<Long> itemIdList;

    private byte numberItems;

    private long purchasedPackId;

    private String entitlementQuantities;
}
