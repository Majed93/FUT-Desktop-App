Meta:

Narrative:
As a user of the FUT Desktop App
I want to be able to view the player list section
So that i can manage my list of players

Lifecycle:
Before:
Scope: STORY
Given the app is loaded
Then the loader has disappeared
And verify the app has fully loaded
Given I create list test.players
Then I add 5 random players to test.players
!-- Don't think we need to login in cause it's only player lists?
!-- Given accounts exist
!-- When the simulator is trained to have 100000 coins
!-- And the simulator is trained to succeed login
!-- Then I select account testAccount1@test.com and platform Xbox One to login with
!-- And I click login to login to FUT
!-- When the loader has disappeared
!-- Then I see currently logged in account is testAccount1@test.com

After:
Scope: STORY
Outcome: ANY
When I delete list test.players
Then the app is closed

Scenario: View player lists
Then the loader has disappeared
Given I navigate to the player-list section
Then I verify i am in the players-list section
And I can see the Player Management heading
And I can see the player list table