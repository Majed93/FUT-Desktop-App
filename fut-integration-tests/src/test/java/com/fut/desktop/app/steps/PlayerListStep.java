package com.fut.desktop.app.steps;

import com.fut.desktop.app.operation.PlayerListOperations;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PlayerListStep {

    private final PlayerListOperations playerListOperations;

    public PlayerListStep(PlayerListOperations playerListOperations) {
        this.playerListOperations = playerListOperations;
    }

    @Given("I create list $fileName")
    public void iCreateList(String fileName) {
        playerListOperations.createList(fileName);
    }

    @Then("I add $numberOfPlayers random players to $listName")
    public void iAddRandomPlayersToList(Integer numberOfPlayers, String listName) {
        playerListOperations.addRandomPlayers(numberOfPlayers, listName);
    }

    @Then("I can see the player list table")
    public void iCanSeePlayerListTable() {
        playerListOperations.verifyVisibilityOfPlayerListTable();
    }

    @When("I delete list $fileName")
    public void deleteList(String listName) {
        playerListOperations.deleteList(listName);
    }
}
