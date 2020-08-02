Meta:

Narrative:
As a user of the FUT Desktop App
I want to be able to see when my account needs action on the web app
So that I am not banned by EA

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
When the simulator is trained to set captcha verified to false for auto listing
Then the app is closed

Scenario: During listing, action needed warning appears
When the trade pile is trained to have 3 unlisted items from list test.players
Given I navigate to the accounts section
Then the loader has disappeared
Given I navigate to the trade-pile section
Then I verify i am in the trade-pile section
Then the loader has disappeared
And I can see the trade pile has 3 items
When the simulator is trained to set captcha verified to true for auto listing
Then I click the autolist unlisted button
And I can see the action needed modal with Captcha Verify message for account testAccount1@test.com
And I dismiss the action needed modal
And I can 3 unlisted items
Scenario: During bidding, action needed warning appears
