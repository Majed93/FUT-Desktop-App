package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonFormat
@AllArgsConstructor
public class ShardInfo extends Shard implements Serializable {

    private static final long serialVersionUID = 3887501164552212752L;
}
