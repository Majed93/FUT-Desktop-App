package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonFormat
public class StoreResponse {
    private String id;
    private List<Pack> purchase;
    private String timeStamp;
}
