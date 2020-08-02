package com.fut.desktop.app.parameters;

import java.util.ArrayList;
import java.util.Collection;

public class FitnessCard extends SearchParameterBase<Long> {
    public final static long ShinyBronzeFitness = 1615614740;
    public final static long ShinySilverFitness = 1615614741;
    public final static long ShinyGoldFitness = 1615614742;
    public final static long NonShinyBronzeFitness = 1615614737;
    public final static long NonShinySilverFitness = 1615614738;
    public final static long NonShinyGoldFitness = 1615614739;


    public FitnessCard(String description, long value) {
        this.description = description;
        this.value = value;
    }

    public static Collection<FitnessCard> getAll() {
        Collection<FitnessCard> fitnessCards = new ArrayList<>();

        fitnessCards.add(new FitnessCard("Shiny Bronze Fitness", ShinyBronzeFitness));
        fitnessCards.add(new FitnessCard("Shiny Silver Fitness", ShinySilverFitness));
        fitnessCards.add(new FitnessCard("Shiny Gold Fitness", ShinyGoldFitness));
        fitnessCards.add(new FitnessCard("Non-Shiny Bronze Fitness", NonShinyBronzeFitness));
        fitnessCards.add(new FitnessCard("Non-Shiny Silver Fitness", NonShinySilverFitness));
        fitnessCards.add(new FitnessCard("Non-Shiny Gold Fitness", NonShinyGoldFitness));

        return fitnessCards;
    }

}
