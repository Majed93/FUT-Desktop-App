package com.fut.desktop.app.restObjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.parameters.Level;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonFormat
@Getter
@Setter
public class AutoBidRequest extends SearchBody {

    /**
     * The bid criteria
     */
    private BidCriteria bidCriteria;

    /**
     * Empty constructor.
     */
    public AutoBidRequest() {
        super();
    }

    /**
     * Constructor.
     */
    public AutoBidRequest(Integer id, List<String> list, List<Player> players, Level level, BidCriteria bidCriteria) {
        super(id, list, players, level);
        this.bidCriteria = bidCriteria;
    }
}
