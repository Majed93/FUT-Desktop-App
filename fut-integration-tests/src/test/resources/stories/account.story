Meta:

Narrative:
As a user of the FUT Desktop App
I want to be able to manage my FUT accounts
So that I can manage my FUT accounts

Lifecycle:
Before:
Scope: STORY
Given the app is loaded
Then the loader has disappeared
And verify the app has fully loaded
!-- Before scenario
Scope: SCENARIO
Given accounts exist
After:
Scope: STORY
Outcome: ANY
Then the app is closed
!-- Before scenario
Scope: SCENARIO
Given accounts are cleared

Scenario: No accounts should be displayed
Given I navigate to the accounts section
And accounts are cleared
Then I can see 0 accounts in the table


Scenario: Add an account
Given the loader has disappeared
And accounts are cleared
When I click the add account button
Then the add account modal appears
And I ensure all the fields are cleared for the add modal
And I try add the following account:
|email|password|secretAnswer|2FA|platform
|test@test.com|password|secret|ABCD1234DEFG5678HIJKL|Xbox One
Then I click the add account button in the modal
And I can see 1 account in the table
And I can see account test@test.com in the table with platform Xbox One
And I see the success alert with message Account added successfully.


Scenario: Add an account with same email and platform should fail
Given the loader has disappeared
When I click the add account button
Then the add account modal appears
And I ensure all the fields are cleared for the add modal
And I try add the following account:
|email|password|secretAnswer|2FA|platform
|testAccount1@test.com|password|secret|ABCD1234DEFG5678HIJKL|Xbox One
Then I click the add account button in the modal
And I can see there was an error adding the account


Scenario: Add an account with same email and different platform should succeed
Given the loader has disappeared
Then I ensure the modal is closed
When I click the add account button
Then the add account modal appears
And I ensure all the fields are cleared for the add modal
And I try add the following account:
|email|password|secretAnswer|2FA|platform
|test@test.com|password|secret|ABCD1234DEFG5678HIJKL|PS4
Then I click the add account button in the modal
And I can see account test@test.com in the table with platform PS4
And I see the success alert with message Account added successfully.


Scenario: View and Update an account
Given the loader has disappeared
Then I ensure the modal is closed
When I click on account testAccount1@test.com with platform Xbox One
Then the update account modal appears
And I try update the following account:
|email|password|secretAnswer|2FA|platform
|test1@test.com|password1|secret1|WXYZ1234DEFG5678HABCD|PS3
Then I click the update account button in the modal
And I can see account test1@test.com in the table with platform PS3
And I see the success alert with message Account updated successfully.


Scenario: Update an account with same email and same platform which exists, should fail
Given the loader has disappeared
When I click on account testAccount1@test.com with platform Xbox One
Then the update account modal appears
And I try update the following account:
|email|password|secretAnswer|2FA|platform
|testAccount3@test.com|password1|secret1|WXYZ1234DEFG5678HABCD|Xbox One
Then I click the update account button in the modal
And I can see there was an error updating the account


Scenario: Update an account with same email and different platform should succeed
Given the loader has disappeared
Then I ensure the modal is closed
When I click on account testAccount1@test.com with platform Xbox One
Then the update account modal appears
And I try update the following account:
|email|password|secretAnswer|2FA|platform
|testAccount1@test.co.uk|password1|secret1|WXYZ1234DEFG5678HABCD|PC
Then I click the update account button in the modal
And I can see account testAccount1@test.co.uk in the table with platform PC
And I see the success alert with message Account updated successfully.


Scenario: Delete an account
Given the loader has disappeared
Then I ensure the modal is closed
When I click on account testAccount1@test.com with platform Xbox One
Then the update account modal appears
And I click the delete button to delete the account
And I see the success alert with message Account deleted successfully.
And I can no longer see account testAccount1@test.com with platform Xbox One in the table


Scenario: Refresh accounts list
Given the loader has disappeared
And accounts are cleared
When account list is refreshed
Then I can see 0 accounts in the table
Given accounts exist
When account list is refreshed
Then I can see 6 accounts in the table