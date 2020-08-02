package com.fut.desktop.app.parameters;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class Position extends SearchParameterBase<String> {
    public final static String Defenders = "defense";

    public final static String Midfielders = "midfield";

    public final static String Attackers = "attacker";

    public final static String GoalKeeper = "GK";

    public final static String RightWingBack = "RWB";

    public final static String RightBack = "RB";

    public final static String CenterBack = "CB";

    public final static String LeftBack = "LB";

    public final static String LeftWingBack = "LWB";

    public final static String CentralDefensiveMidfielder = "CDM";

    public final static String RightMidfielder = "RM";

    public final static String CentralMidfielder = "CM";

    public final static String LeftMidfielder = "LM";

    public final static String CentralAttackingMidfielder = "CAM";

    public final static String RightForward = "RF";

    public final static String CentralForward = "CF";

    public final static String LeftForward = "LF";

    public final static String RightWinger = "RW";

    public final static String Striker = "ST";

    public final static String LeftWinger = "LW";

    public Position(String description, String value) {
        this.description = description;
        this.value = value;
    }

    @JsonCreator
    public static Position fromValue(String pos) {
        for (Position c : getAll()) {
            if (pos.equals(c.value)) {
                return c;
            }
        }
        return new Position("Goalkeeper", GoalKeeper);
    }

    public static Collection<Position> getAll() {
        Collection<Position> positions = new ArrayList<>();

        positions.add(new Position("Defenders", Defenders));
        positions.add(new Position("Midfielders", Midfielders));
        positions.add(new Position("Attackers", Attackers));
        positions.add(new Position("Goalkeeper", GoalKeeper));
        positions.add(new Position("Right wing back", RightWingBack));
        positions.add(new Position("Right back", RightBack));
        positions.add(new Position("Center back", CenterBack));
        positions.add(new Position("Left back", LeftBack));
        positions.add(new Position("Left wing back", LeftWingBack));
        positions.add(new Position("Central defensive midfielder", CentralDefensiveMidfielder));
        positions.add(new Position("Right midfielder", RightMidfielder));
        positions.add(new Position("Central midfielder", CentralMidfielder));
        positions.add(new Position("Left midfielder", LeftMidfielder));
        positions.add(new Position("Central attacking midfielder", CentralAttackingMidfielder));
        positions.add(new Position("Right forward", RightForward));
        positions.add(new Position("Center forward", CentralForward));
        positions.add(new Position("Left forward", LeftForward));
        positions.add(new Position("Right winger", RightWinger));
        positions.add(new Position("Striker", Striker));
        positions.add(new Position("Left winger", LeftWinger));

        return positions;
    }

    public static Collection<Position> getAllWithoutOverall() {
        Collection<Position> positions = new ArrayList<>();

        positions.add(new Position("Goalkeeper", GoalKeeper));
        positions.add(new Position("Right wing back", RightWingBack));
        positions.add(new Position("Right back", RightBack));
        positions.add(new Position("Center back", CenterBack));
        positions.add(new Position("Left back", LeftBack));
        positions.add(new Position("Left wing back", LeftWingBack));
        positions.add(new Position("Central defensive midfielder", CentralDefensiveMidfielder));
        positions.add(new Position("Right midfielder", RightMidfielder));
        positions.add(new Position("Central midfielder", CentralMidfielder));
        positions.add(new Position("Left midfielder", LeftMidfielder));
        positions.add(new Position("Central attacking midfielder", CentralAttackingMidfielder));
        positions.add(new Position("Right forward", RightForward));
        positions.add(new Position("Center forward", CentralForward));
        positions.add(new Position("Left forward", LeftForward));
        positions.add(new Position("Right winger", RightWinger));
        positions.add(new Position("Striker", Striker));
        positions.add(new Position("Left winger", LeftWinger));

        return positions;
    }

    /**
     * Get a random position
     *
     * @return Random position
     */
    public static String random() {
        Random random = new Random();
        List<Position> positions = new ArrayList<>(getAll());
        int i = random.nextInt(positions.size());
        return positions.get(i).getValue();
    }
}
