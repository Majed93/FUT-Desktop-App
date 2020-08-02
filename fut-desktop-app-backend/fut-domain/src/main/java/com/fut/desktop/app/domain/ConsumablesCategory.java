package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonFormat
@AllArgsConstructor
public enum ConsumablesCategory {
    consumablesContractPlayer,
    consumablesContractManager,
    consumablesFormationPlayer,
    consumablesFormationManager,
    consumablesPosition,
    consumablesTraining,
    consumablesTrainingPlayer,
    consumablesTrainingManager,
    consumablesTrainingGk,
    consumablesTrainingPlayerPlayStyle,
    consumablesTrainingGkPlayStyle,
    consumablesTrainingManagerLeagueModifier,
    consumablesHealing,
    consumablesTeamTalksPlayer,
    consumablesTeamTalksTeam,
    consumablesFitnessPlayer,
    consumablesFitnessTeam,
    consumables
}
