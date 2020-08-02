package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonFormat
@AllArgsConstructor
@NoArgsConstructor
public class Shard implements Serializable {

    private static final long serialVersionUID = -3251584263306253545L;

    private String shardId;

    private String clientFacingIpPort;

    private String clientProtocol;

    private String[] content;

    private String[] platforms;

    private String[] customData1;

    private String[] skus;
}
