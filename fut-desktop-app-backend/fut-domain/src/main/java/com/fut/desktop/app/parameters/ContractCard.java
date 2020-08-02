package com.fut.desktop.app.parameters;

import java.util.ArrayList;
import java.util.Collection;

public class ContractCard extends SearchParameterBase<Long> {
    public static final long ShinyBronzePlayerContract = 1615613740;
    public static final long ShinySilverPlayerContract = 1615613741;
    public static final long ShinyGoldPlayerContract = 1615613742;
    public static final long NonShinyBronzePlayerContract = 1615613737;
    public static final long NonShinySilverPlayerContract = 1615613738;
    public static final long NonShinyGoldPlayerContract = 1615613739;
    public static final long ShinyBronzeManagerContract = 1615613746;
    public static final long ShinySilverManagerContract = 1615613747;
    public static final long ShinyGoldManagerContract = 1615613748;
    public static final long NonShinyBronzeManagerContract = 1615613743;
    public static final long NonShinySilverManagerContract = 1615613744;
    public static final long NonShinyGoldManagerContract = 1615613745;

    public ContractCard(String description, long value)
    {
        this.description = description;
        this.value = value;
    }

    public static Collection<ContractCard> getAll(){
        Collection<ContractCard> contractCards = new ArrayList<>();

        contractCards.add(new ContractCard("Shiny Bronze Player Contract", ShinyBronzePlayerContract));
        contractCards.add(new ContractCard("Shiny Silver Player Contract", ShinySilverPlayerContract));
        contractCards.add(new ContractCard("Shiny Gold Player Contract", ShinyGoldPlayerContract));
        contractCards.add(new ContractCard("Non-Shiny Bronze Player Contract", NonShinyBronzePlayerContract));
        contractCards.add(new ContractCard("Non-Shiny Silver Player Contract", NonShinySilverPlayerContract));
        contractCards.add(new ContractCard("Non-Shiny Gold Player Contract", NonShinyGoldPlayerContract));
        contractCards.add(new ContractCard("Shiny Bronze Manager Contract", ShinyBronzeManagerContract));
        contractCards.add(new ContractCard("Shiny Silver Manager Contract", ShinySilverManagerContract));
        contractCards.add(new ContractCard("Shiny Gold Manager Contract", ShinyGoldManagerContract));
        contractCards.add(new ContractCard("Non-Shiny Bronze Manager Contract", NonShinyBronzeManagerContract));
        contractCards.add(new ContractCard("Non-Shiny Silver Manager Contract", NonShinySilverManagerContract));
        contractCards.add(new ContractCard("Non-Shiny Gold Manager Contract", NonShinyGoldManagerContract));

        return contractCards;
    }
}
