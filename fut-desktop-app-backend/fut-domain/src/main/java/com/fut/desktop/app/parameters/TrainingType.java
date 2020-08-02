package com.fut.desktop.app.parameters;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class TrainingType extends SearchParameterBase<String> {
    public final static String PlayerTraining = "playerTraining";

    public final static String GkTraining = "GKTraining";

    public final static String Position = "position";

    public final static String ChemistryStyles = "playStyle";

    public final static String ManagerLeagues = "managerLeagueModifier";

    public TrainingType(String description, String value) {
        this.description = description;
        this.value = value;
    }

    public static Collection<TrainingType> getAll() {
        List<TrainingType> trainingTypeEnumeration = new ArrayList<>();

        trainingTypeEnumeration.add(new TrainingType("playerTraining", PlayerTraining));
        trainingTypeEnumeration.add(new TrainingType("GKTraining", GkTraining));
        trainingTypeEnumeration.add(new TrainingType("position", Position));
        trainingTypeEnumeration.add(new TrainingType("playStyle", ChemistryStyles));
        trainingTypeEnumeration.add(new TrainingType("managerLeagueModifier", ManagerLeagues));

        return trainingTypeEnumeration;
    }
}
