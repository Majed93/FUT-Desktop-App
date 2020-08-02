package com.fut.desktop.app.utils;

import com.fut.desktop.app.restObjects.ProfitMargin;

import java.util.ArrayList;
import java.util.List;

public final class ProfitMarginUtil {


    public static List<ProfitMargin> generateDefaultProfitMargins() {
        List<ProfitMargin> profitMargins = new ArrayList<>();

        ProfitMargin profitMargin1 = new ProfitMargin(1000, 300L, 500L, 1);
        profitMargins.add(profitMargin1);

        ProfitMargin profitMargin2 = new ProfitMargin(1500, 500L, 750L, 1);
        profitMargins.add(profitMargin2);

        ProfitMargin profitMargin3 = new ProfitMargin(2000, 800L, 900L, 1);
        profitMargins.add(profitMargin3);

        ProfitMargin profitMargin4 = new ProfitMargin(5000, 900L, 1000L, 1);
        profitMargins.add(profitMargin4);

        ProfitMargin profitMargin5 = new ProfitMargin(10000, 1200L, 1500L, 1);
        profitMargins.add(profitMargin5);

        ProfitMargin profitMargin6 = new ProfitMargin(20000, 1900L, 2100L, 1);
        profitMargins.add(profitMargin6);

        ProfitMargin profitMargin7 = new ProfitMargin(30000, 2900L, 3100L, 1);
        profitMargins.add(profitMargin7);

        ProfitMargin profitMargin8 = new ProfitMargin(40000, 3900L, 4100L, 1);
        profitMargins.add(profitMargin8);

        ProfitMargin profitMargin9 = new ProfitMargin(50000, 4900L, 5100L, 1);
        profitMargins.add(profitMargin9);

        ProfitMargin profitMargin10 = new ProfitMargin(100000, 8000L, 9000L, 1);
        profitMargins.add(profitMargin10);

        ProfitMargin profitMargin11 = new ProfitMargin(15000000, 99000L, 100000L, 1);
        profitMargins.add(profitMargin11);


        return profitMargins;
    }
}
