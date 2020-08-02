package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonFormat
public class CreditsResponse {

    private long credits;

    private List<Currency> currencies;

    private UnopenedPacks unopenedPacks;

    private BidTokens bidTokens;

    private double futCashBalance;
}
