package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

@Getter
@Setter
@JsonFormat
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ListAuctionResponse {

    private long id;

    private String idStr;
}
