package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.constants.Currency;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class PackDetails {
    private Currency currency;

    private long packId;

    private long useCredits;

    private Boolean usePreOrder;

    public PackDetails(Currency currency, long packId, long useCredits, Boolean usePreOrder) {
        if (packId <= 0) {
            throw new IllegalArgumentException("PackDetails: Invalid Pack Id");
        }

        this.currency = currency;
        this.packId = packId;
        this.useCredits = useCredits;
        this.usePreOrder = usePreOrder;
    }
}
