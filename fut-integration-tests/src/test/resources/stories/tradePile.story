Meta:

Narrative:
As a user of the FUT Desktop App
I want to be able to view my trade pile
So that I can manage player listings

Lifecycle:
Before:
Scope: STORY
Given the app is loaded
Then the loader has disappeared
And verify the app has fully loaded
Given I create list test.players
Then I add 5 random players to test.players
Given accounts exist
When the simulator is trained to have 100000 coins
And the simulator is trained to succeed login
Then I select account testAccount1@test.com and platform Xbox One to login with
And I click login to login to FUT
When the loader has disappeared
Then I see currently logged in account is testAccount1@test.com

After:
Scope: STORY
Outcome: ANY
Given the trade pile is trained to have 0 items
When I delete list test.players
Then the app is closed


Scenario: Trade pile empty
Given the trade pile is trained to have 0 items
Given I navigate to the accounts section
Then the loader has disappeared
Given I navigate to the trade-pile section
Then I verify i am in the trade-pile section
Then the loader has disappeared
And I can see the trade pile has 0 items


Scenario: Trade pile contains X amount of items
Given the trade pile is trained to have 10 items
Given I navigate to the accounts section
Then the loader has disappeared
Given I navigate to the trade-pile section
Then I verify i am in the trade-pile section
Then the loader has disappeared
And I can see the trade pile has 10 items


Scenario: Trade pile contains 100 amount of items
Given the trade pile is trained to have 100 items
Given I navigate to the accounts section
Then the loader has disappeared
Given I navigate to the trade-pile section
Then I verify i am in the trade-pile section
Then the loader has disappeared
And I can see the trade pile has 100 items


Scenario: Autolist from trade pile
Given the trade pile is trained to have 5 items
When the trade pile is trained to have 5 unlisted items from list test.players
Given I navigate to the accounts section
Then the loader has disappeared
Given I navigate to the trade-pile section
Then I verify i am in the trade-pile section
Then the loader has disappeared
And I can see the trade pile has 10 items
And I click the autolist unlisted button
And I wait for the listing to finish
And I can see 5 items listed


Scenario: Remove sold items
Given the trade pile is trained to have 20 items
When the trade pile is trained to have 5 sold items
Given I navigate to the accounts section
Then the loader has disappeared
Given I navigate to the trade-pile section
Then I verify i am in the trade-pile section
Then the loader has disappeared
And I can see the trade pile has 25 items
And I can see 5 items sold
When I click remove sold
Then the loader has disappeared
And I can see the trade pile has 20 items
And I can see no sold items
