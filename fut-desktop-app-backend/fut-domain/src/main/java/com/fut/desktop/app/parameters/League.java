package com.fut.desktop.app.parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

// TODO: Refine
@SuppressWarnings("WeakerAccess")
public class League extends SearchParameterBase<Long> {
    public final static long Any = 0;
    public final static long AlkaSuperliga = 1;
    public final static long Allsvenskan = 56;
    public final static long Bundesliga = 19;
    public final static long Bundesliga2 = 20;
    public final static long CalcioA = 31;
    public final static long CalcioB = 32;
    public final static long CeskaLiga = 319;
    public final static long CampScotiabank = 335;
    public final static long DawryJameel = 350;
    public final static long DominosLigue2 = 17;
    public final static long EFLChampionship = 14;
    public final static long EFLLeagueOne = 60;
    public final static long EFLLeagueTwo = 61;
    public final static long Ekstraklasa = 66;
    public final static long Eredivisie = 10;
    public final static long Finnliiga = 322;
    public final static long HellasLiga = 63;
    public final static long HyundaiALeague = 351;
    public final static long KLeagueClassic = 83;
    public final static long LaLiga123 = 54;
    public final static long LaLigaSantander = 53;
    public final static long Legends = 2118;
    public final static long LIGABancomerMX = 341;
    public final static long LigaDimayor = 336;
    public final static long LigaNOS = 308;
    public final static long Ligue1 = 16;
    public final static long MLS = 39;
    public final static long MeijiYasudaJ1League = 349;
    public final static long OBundesliga = 80;
    public final static long PremierLeague = 13;
    public final static long PrimeraDivision = 353;
    public final static long ProLeague = 4;
    public final static long RSL = 189;
    public final static long RussianLeague = 67;
    public final static long ScottishPremiership = 50;
    public final static long SouthAfricanFL = 347;
    public final static long SSEAirtricityLeague = 65;
    public final static long SuperLig = 68;
    public final static long Tippeligaen = 41;
    public final static long UkrayinaLiha = 332;
    public final static long UltimateTeam = 2134;

    public League(String description, long value) {
        this.description = description;
        this.value = value;
    }

    public static Collection<League> getAll() {
        Collection<League> leagues = new ArrayList<>();

        leagues.add(new League("Alka Superliga", AlkaSuperliga));
        leagues.add(new League("Allsvenskan", Allsvenskan));
        leagues.add(new League("Bundesliga", Bundesliga));
        leagues.add(new League("Bundesliga 2", Bundesliga2));
        leagues.add(new League("Calcio A", CalcioA));
        leagues.add(new League("Calcio B", CalcioB));
        leagues.add(new League("CeskaLiga", CeskaLiga));
        leagues.add(new League("Camp. Scotiabank", CampScotiabank));
        leagues.add(new League("Dawry Jameel", DawryJameel));
        leagues.add(new League("Domino's Ligue 2", DominosLigue2));
        leagues.add(new League("EFL Championship", EFLChampionship));
        leagues.add(new League("EFL League One", EFLLeagueOne));
        leagues.add(new League("EFL League Two", EFLLeagueTwo));
        leagues.add(new League("T-Mobile Ekstraklasa", Ekstraklasa));
        leagues.add(new League("Eredivisie", Eredivisie));
        leagues.add(new League("Finnliiga", Finnliiga));
        leagues.add(new League("Hellas Liga", HellasLiga));
        leagues.add(new League("Hyundai A-League", HyundaiALeague));
        leagues.add(new League("K LEAGUE Classic", KLeagueClassic));
        leagues.add(new League("LaLiga 1 | 2 | 3", LaLiga123));
        leagues.add(new League("LaLiga Santander", LaLigaSantander));
        leagues.add(new League("Legends", Legends));
        leagues.add(new League("LIGA Bancomer MX", LIGABancomerMX));
        leagues.add(new League("Liga Dimayor", LigaDimayor));
        leagues.add(new League("Liga NOS", LigaNOS));
        leagues.add(new League("Ligue 1", Ligue1));
        leagues.add(new League("Major League Soccer", MLS));
        leagues.add(new League("Meiji Yasuda J1 League", MeijiYasudaJ1League));
        leagues.add(new League("Osterreichische Fussball-Bundesliga", OBundesliga));
        leagues.add(new League("Premier League", PremierLeague));
        leagues.add(new League("Primera Division", PrimeraDivision));
        leagues.add(new League("Belgium Pro League", ProLeague));
        leagues.add(new League("Raiffeisen Super League", RSL));
        leagues.add(new League("Russian League", RussianLeague));
        leagues.add(new League("Scottish Premiership", ScottishPremiership));
        leagues.add(new League("South African FL", SouthAfricanFL));
        leagues.add(new League("SSE Airtricity League", SSEAirtricityLeague));
        leagues.add(new League("Super Lig", SuperLig));
        leagues.add(new League("Tippeligaen", Tippeligaen));
        leagues.add(new League("Ukrayina Liha", UkrayinaLiha));
        leagues.add(new League("Ultimate Team", UltimateTeam));

        return leagues;
    }

    /**
     * Get a random league
     *
     * @return Random League value
     */
    public static Integer getRandom() {
        Random random = new Random();
        List<League> leagues = new ArrayList<>(getAll());
        int i = random.nextInt(leagues.size());
        return Math.toIntExact(leagues.get(i).getValue());
    }
}
