package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@JsonFormat
@AllArgsConstructor
@NoArgsConstructor
public class Shards implements Serializable {

    private static final long serialVersionUID = -3543861288094867965L;

    private List<ShardInfo> shardInfo;
}
