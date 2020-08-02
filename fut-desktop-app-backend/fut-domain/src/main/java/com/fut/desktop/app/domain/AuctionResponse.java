package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@JsonFormat
@ToString
public class AuctionResponse {

    private List<AuctionInfo> auctionInfo;

    private BidTokens bidTokens;

    private Long credits;

    private List<Currency> currencies;

    private List<DuplicateItem> duplicateItemIdList;

    private int total;

    private String errorState;
}
