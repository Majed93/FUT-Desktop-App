package com.fut.desktop.app.restObjects;

import com.fut.desktop.app.domain.AuctionResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BiddingObject {

    private boolean binned;

    private boolean sendToTrade;

    private int unAssigned;

    private boolean unassignedItemsLeft;

    private AuctionResponse response;
}
