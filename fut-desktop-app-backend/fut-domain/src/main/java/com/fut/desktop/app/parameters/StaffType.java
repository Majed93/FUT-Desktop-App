package com.fut.desktop.app.parameters;

import java.util.ArrayList;
import java.util.Collection;

public class StaffType extends SearchParameterBase<String> {

    public static final String Manager = "manager";

    public static final String HeadCoach = "headCoach";

    public static final String GkCoach = "GkCoach";

    public static final String FitnessCoach = "fitnessCoach";

    public static final String PhysioCoach = "physio";

    public StaffType(String description, String value) {
        this.description = description;
        this.value = value;
    }

    public static Collection<StaffType> getAll() {
        Collection<StaffType> staffTypes = new ArrayList<>();

        staffTypes.add(new StaffType("manager", Manager));
        staffTypes.add(new StaffType("headCoach", HeadCoach));
        staffTypes.add(new StaffType("GkCoach", GkCoach));
        staffTypes.add(new StaffType("fitnessCoach", FitnessCoach));
        staffTypes.add(new StaffType("physio", PhysioCoach));

        return staffTypes;
    }
}
