Meta:

Narrative:
As a user of the FUT Desktop App
I want to be able to login to my FUT account
So that I can perform actions like the Web App

Lifecycle:
Before:
Scope: STORY
Given the app is loaded
Then the loader has disappeared
And verify the app has fully loaded
Given accounts exist
After:
Scope: STORY
Outcome: ANY
Then the app is closed


Scenario: I fail to login to my FUT account
When the loader has disappeared
And the simulator is trained to have 100000 coins
And the simulator is trained to fail login
Then I select account testAccount2@test.com and platform Xbox One to login with
And I click login to login to FUT
When the loader has disappeared
Then I see the danger alert with message Unable to login. Check account details are correct and you can access the web app.


Scenario: I can successfully login to my FUT account
When the loader has disappeared
And the simulator is trained to have 100000 coins
And the simulator is trained to succeed login
Then I select account testAccount2@test.com and platform Xbox One to login with
And I click login to login to FUT
When the loader has disappeared
Then I see currently logged in account is testAccount2@test.com
And I see the coins balance for the current account testAccount2@test.com - Xbox One is as expected
And I see the total coins

