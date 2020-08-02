package com.fut.api.fut.rest;

import com.fut.api.fut.client.IFutClient;
import com.fut.desktop.app.domain.AuctionInfo;
import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.parameters.PlayerSearchParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Handle transfer market operations
 */
@RequestMapping("/io/transferMarket")
@RestController
@Slf4j
public class TransferMarketIORestController {

    private final IFutClient futClient;

    @Autowired
    public TransferMarketIORestController(IFutClient futClient) {
        this.futClient = futClient;
    }

    @PostMapping(value = "/search/{first}")
    @ResponseBody
    public AuctionResponse search(@RequestBody PlayerSearchParameters params, @PathVariable("first") Boolean first) throws Exception {
        return futClient.search(params, first).get();
    }

    @GetMapping(value = "/pageSize")
    @ResponseBody
    public Integer getPageSize() {
        return futClient.getFutRequestFactories().getPageSize();
    }

    @PostMapping(value = "/bidItem/{bidAmount}")
    @ResponseBody
    public AuctionResponse bidItem(@RequestBody AuctionInfo info, @PathVariable("bidAmount") Long bidAmount) throws Exception {
        if (bidAmount == 0) {
            bidAmount = info.CalculateBid();
        }
        return futClient.bidItem(info.getTradeId(), bidAmount).get();
    }
}
