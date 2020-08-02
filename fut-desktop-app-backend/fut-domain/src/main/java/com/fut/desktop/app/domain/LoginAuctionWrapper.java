package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonFormat
@NoArgsConstructor
@AllArgsConstructor
public class LoginAuctionWrapper {

    private LoginResponse loginResponse;

    private HomeWrapper homeWrapper;

    private List<PileSize> pileSizes;
}
