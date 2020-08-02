package com.fut.desktop.app.parameters;

import java.util.ArrayList;
import java.util.Collection;

public class ClubInfoType extends SearchParameterBase<String> {
    public final static String Kit = "kit";

    public final static String Badge = "badge";

    public ClubInfoType(String description, String value) {
        this.description = description;
        this.value = value;
    }

    public static Collection<ClubInfoType> getAll() {
        Collection<ClubInfoType> clubInfoTypes = new ArrayList<>();

        clubInfoTypes.add(new ClubInfoType("kit", Kit));
        clubInfoTypes.add(new ClubInfoType("badge", Badge));

        return clubInfoTypes;
    }
}
