package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.List;

@Getter
@Setter
@JsonFormat
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RelistResponse {
    private List<ListAuctionResponse> tradeIdList;
}
