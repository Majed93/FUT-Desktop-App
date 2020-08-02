!-- Starts with 1 so it runs first.
Meta:

Narrative:
As a user of the FUT Desktop App
I want to be able to view the main page
So that i can login

Lifecycle:
Before:
Scope: STORY
Given the app is loaded
Then the loader has disappeared
After:
Scope: STORY
Outcome: ANY
Then the app is closed

Scenario: The application starts up
Then the loader has disappeared
And I can see the buttons
And the fields are empty
And the login button is disabled

Scenario: The user fails to login
When the loader has disappeared
Then I enter the login details:
|email|key
|test@test.com|ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890
And the license server fails to authorise
And I click login
And I see error Invalid credentials when logging in

Scenario: the user successfully logs in
When the loader has disappeared
Then I enter the login details:
|email|key
|test@test.com|ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890
And the license server true to authorise
And I click login
And I see the success alert with message Successfully logged in.
And I can see the accounts section
And I can see the Account List heading

Scenario: The app auto logs in once the user has logged in once before
When the user waits 4 seconds
Given the app is closed
Given the app is loaded
Then the loader has disappeared
And I see the success alert with message Successfully logged in.
And I can see the accounts section
And I can see the Account List heading
