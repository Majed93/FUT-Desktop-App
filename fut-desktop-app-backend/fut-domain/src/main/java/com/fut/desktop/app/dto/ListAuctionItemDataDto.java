package com.fut.desktop.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Object which contains id of item to list.
 */
@Getter
@Setter
@JsonFormat
@AllArgsConstructor
@NoArgsConstructor
public class ListAuctionItemDataDto {

    /**
     * Id of item to list.
     */
    private long id;
}
