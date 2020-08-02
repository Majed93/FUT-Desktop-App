package com.fut.desktop.app.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@JsonFormat
@AllArgsConstructor
public enum PinEventId {
    WebApp_Load("connection"), // Not specific to web app, sends en=boot_start
    WebApp_BootStart("boot_start"),  // Not specific to web app
    Get_Banners("Banners"),
    WebApp_Home("home"),
    WebApp_Login("login"),
    TOTW("TOTW"),
    WebApp_Generations("Generations"),
    WebApp_UnassignedItems("Unassigned Items"),
    WebApp_TransferTargets("transfer targets"),
    WebApp_TransferList("transfer List"),
    WebApp_TransferMarketSearchResults("Transfer Market Search Results"),
    WebApp_Players("Club - Players"),
    WebApp_Staff("Club - Staff"),
    WebApp_Items("Club - Items"),
    WebApp_Consumables("Club - Consumables"),
    WebApp_Leaderboards("Leaderboards - Main"),
    WebApp_LeaderboardsEarnings("Leaderboards - Match Earnings"),
    WebApp_LeaderboardsTransferProfit("Leaderboards - Transfer Profit"),
    WebApp_LeaderboardsClubValue("Leaderboards - Club Value"),
    WebApp_LeaderboardsTopSquad("Leaderboards - Top Squad"),
    WebApp_MainStore("Store - Main"),
    WebApp_GoldStore("Store - Gold"),
    WebApp_SilverStore("Store - Silver"),
    WebApp_BronzeStore("Store - Bronze"),
    CompanionApp_AppOpened("Companion App - OPEN"),
    CompanionApp_Connect("Companion App - CONNECT"),
    CompanionApp_Connected("Companion App - CONNECTED"),
    App_Home("Hub - Home"),
    CompanionApp_HubSquads("Hub - Squads"),
    CompanionApp_HubDraft("Hub - Draft"),
    App_HubTransfers("Hub - Transfers"),
    CompanionApp_HubStore("Hub - Store"),
    CompanionApp_HubUnassigned("Hub - Unassigned"),
    CompanionApp_UnassignedItems("Unassigned Items - List View"),
    CompanionApp_UnassignedItems_Detailed("Unassigned Items - Detail View"),
    App_TransferTargets("Transfer Targets - List View"),
    CompanionApp_TransferTargets_Detailed("Transfer Targets - Detail View"),
    App_TransferList("Transfer List - List View"),
    App_ItemDetailView("Item - Detail View"),
    CompanionApp_TransferList_Detailed("Transfer List - Detail View"),
    App_TransferMarketResults("Transfer Market Results - List View"),
    CompanionApp_TransferMarketResults_Detailed("Transfer Market Results - Detail View"),
    CompanionApp_ActiveSquad_Details("Active Squad - Details"),
    CompanionAppp_ActiveSquad_SwapPlayer("Active Squad - Swap Player"),
    WebApp_PackCategory("Store - Pack Category"),
    WebApp_PackDetails("Store - Pack Details"),
    CompanionApp_Players("Club - Players - List View"),
    CompanionApp_Players_Detailed("Club - Players - Detail View"),
    CompanionApp_Staff("Club - Staff - List View"),
    CompanionApp_Staff_Detailed("Club - Staff - Detail View"),
    CompanionApp_Club("Club - Club Items - List View"),
    CompanionApp_Consumables("Club - Club Consumables - List View"),
    CompanionApp_Club_Detailed("Club - Club - Detail View"),
    CompanionApp_EASFC_News("EASFC - News"),
    CompanionApp_EASFC_PreviewSquad("EASFC - News - Preview Squad"),
    Generic_TransferMarketSearch("Transfer Market Search"),
    Generic_SquadList("Squad List - My Squads"),
    Generic_ConceptSquadList("Squad List - Concept Squads"),
    Generic_ActiveSquad("Active Squad"),
    UnassignedItems("Unassigned Items - List View");
    private String pinEventId;

    @JsonCreator
    public static PinEventId fromValue(String pinEvent) {
        for (PinEventId p : values()) {
            if (Objects.equals(pinEvent, p.pinEventId)) {
                return p;
            }
        }

        return PinEventId.App_Home;
    }
}
