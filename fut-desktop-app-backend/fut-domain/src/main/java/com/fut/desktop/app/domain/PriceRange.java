package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat
public class PriceRange {
    private String source;
    private long defId;
    private long itemId;
    private long minPrice;
    private long maxPrice;
}
