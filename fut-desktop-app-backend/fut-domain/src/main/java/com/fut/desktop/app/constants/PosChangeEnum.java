package com.fut.desktop.app.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonFormat
@AllArgsConstructor
public enum  PosChangeEnum {
    @JsonProperty("LWB_LB")
    LWB_LB,
    @JsonProperty("LB_LWB")
    LB_LWB,

    @JsonProperty("RWB_RB")
    RWB_RB,
    @JsonProperty("RB_RWB")
    RB_RWB,

    @JsonProperty("LM_LW")
    LM_LW,
    @JsonProperty("LW_LM")
    LW_LM,

    @JsonProperty("RM_RW")
    RM_RW,
    @JsonProperty("RW_RM")
    RW_RM,

    @JsonProperty("LW_LF")
    LW_LF,
    @JsonProperty("LF_LW")
    LF_LW,

    @JsonProperty("RW_RF")
    RW_RF,
    @JsonProperty("RF_RW")
    RF_RW,

    @JsonProperty("CM_CAM")
    CM_CAM,
    @JsonProperty("CAM_CM")
    CAM_CM,

    @JsonProperty("CM_CDM")
    CM_CDM,
    @JsonProperty("CDM_CM")
    CDM_CM,

    @JsonProperty("CF_CAM")
    CF_CAM,
    @JsonProperty("CAM_CF")
    CAM_CF,

    @JsonProperty("CF_ST")
    CF_ST,
    @JsonProperty("ST_CF")
    ST_CF
}
