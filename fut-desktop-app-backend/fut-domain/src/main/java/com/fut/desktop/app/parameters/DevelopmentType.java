package com.fut.desktop.app.parameters;

import java.util.ArrayList;
import java.util.Collection;

public class DevelopmentType extends SearchParameterBase<String> {
    public final static String Contract = "contract";

    public final static String Fitness = "fitness";

    public final static String Healing = "healing";


    private DevelopmentType(String description, String value) {
        this.description = description;
        this.value = value;
    }

    public static Collection<DevelopmentType> getAll() {
        Collection<DevelopmentType> developmentTypes = new ArrayList<>();

        developmentTypes.add(new DevelopmentType("contract", Contract));
        developmentTypes.add(new DevelopmentType("fitness", Fitness));
        developmentTypes.add(new DevelopmentType("healing", Healing));

        return developmentTypes;
    }
}
