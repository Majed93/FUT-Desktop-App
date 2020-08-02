import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {CUSTOM_ELEMENTS_SCHEMA, DebugElement} from '@angular/core';
import {AccountsComponent} from './accounts.component';
import {HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {GlobalService} from '../../globals/global.service';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgxPaginationModule} from 'ngx-pagination';
import {APP_BASE_HREF} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CookieModule} from 'ngx-cookie';
import {AppRoutingModule} from '../../app-routing.module';
import {MultiselectDropdownModule} from 'angular-2-dropdown-multiselect';
import {AlertModule} from '../../alert';
import {LoaderComponent} from '../../loader/loader.component';
import {AccountsService} from './accounts.service';
import {BrowserModule, By} from '@angular/platform-browser';
import {AccountTestUtils} from '../../../test-utils/account.test.utils';
import {Account} from '../../domain/account';
import {of, throwError} from 'rxjs';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {Platform} from '../../enums/platform';
import {WebSocketService} from '../../services/websocket.service';
import {StateService} from '../../status-modal/state-service';
import {LoginComponent} from '../../login/login.component';
import {MainComponent} from '../main.component';
import {TradePileComponent} from '../trade-pile/trade-pile.component';
import {WatchListComponent} from '../watch-list/watch-list.component';
import {UnassignedComponent} from '../unassigned/unassigned.component';
import {TransferSearchComponent} from '../transfer-search/transfer-search.component';
import {AutoBidComponent} from '../auto-bid/auto-bid.component';
import {PlayersMgmtComponent} from '../players-mgmt/players-mgmt.component';
import {StatusModalComponent} from '../../status-modal/status-modal.component';
import {Message} from '@stomp/stompjs';
import {StatusMessage} from '../../domain/status.message.type';

describe('AccountsComponent', () => {
  let component: AccountsComponent;
  let fixture: ComponentFixture<AccountsComponent>;

  let accountService: AccountsService;
  let globalServiceMock: GlobalService;
  let webSocketService: WebSocketService;

  let spyOnWsConnect;

  let getAllAccountsUrl;

  let testAccount: Account;
  let accounts: Account[];

  let accountTable: DebugElement;
  const ACCOUNT_TABLE_ID = 'account-table';
  const NO_ACCOUNTS_MESSAGE = 'No accounts to display';

  let tableRows: DebugElement[];

  /**
   * Before each test.
   */
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        LoaderComponent,
        AccountsComponent,
        LoginComponent,
        MainComponent,
        TradePileComponent,
        PlayersMgmtComponent,
        WatchListComponent,
        TransferSearchComponent,
        AutoBidComponent,
        StatusModalComponent,
        UnassignedComponent
      ],
      imports: [
        AlertModule,
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        NgxPaginationModule,
        MultiselectDropdownModule,
        HttpClientModule,
        HttpClientTestingModule,
        HttpClientXsrfModule.withOptions({
          cookieName: 'XSRF-TOKEN',
          headerName: 'XSRF-TOKEN'
        }),
        CookieModule.forChild()
      ],
      providers: [
        AccountsService,
        GlobalService,
        StateService,
        WebSocketService,
        {provide: APP_BASE_HREF, useValue: '/'}],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents();
  }));

  /**
   * Before each test
   */
  beforeEach(() => {
    fixture = TestBed.createComponent(AccountsComponent);

    accountService = TestBed.get(AccountsService);
    globalServiceMock = TestBed.get(GlobalService);
    webSocketService = TestBed.get(WebSocketService);

    component = fixture.componentInstance;
    getAllAccountsUrl = globalServiceMock.endpoint + globalServiceMock.ACCOUNT_PATH;
    testAccount = AccountTestUtils.createMockAccount();
    globalServiceMock.csrf_token = '1234567';
    component.accounts = [];
    fixture.detectChanges();
  });

  const sendMessage = (): void => {
    component.isConnected = false;
    const msg: StatusMessage = new StatusMessage('test');
    // Mock out listening to the web service
    const mockResponse: Message = {
      body: JSON.stringify(msg),
      ack: headers => {
      },
      binaryBody: null,
      command: '',
      headers: null,
      isBinaryBody: false,
      nack: headers => {
      }
    };
    spyOnWsConnect = spyOn(webSocketService, 'startListening').and.returnValue(of(mockResponse));
  };

  /**
   * After each test
   */
  afterEach(() => {
    // const modalBackdrop = fixture.debugElement.query(By.css('.modal-backdrop'));
    const elements = document.getElementsByClassName('modal-backdrop');
    for (let i = 0; i < elements.length; i++) {
      elements[i].remove();
    }
    component.ngOnDestroy();
  });

  /**
   * Helper function to inject account
   */
  function injectAccount() {
    accounts = [];
    accounts.push(testAccount);
    globalServiceMock.newAccount(accounts);
    spyOn(accountService, 'getAccounts').and.returnValue(of(accounts));
    fixture.detectChanges();
  }

  /**
   * Helper function to get table
   */
  function getTable() {
    accountTable = fixture.debugElement.query(By.css('#' + ACCOUNT_TABLE_ID));
  }

  /**
   * Helper method to get rows
   */
  function getRows() {
    tableRows = accountTable.query(By.css('tbody')).queryAll(By.css('tr'));
  }

  /**
   * Helper function to click on multiselect
   */
  function clickOnMultiselect(platformType: string, selector: string) {
    // Get the multiselect
    const multiselect = fixture.debugElement.query(By.css('#' + selector));
    multiselect.nativeElement.value = platformType;
    multiselect.nativeElement.dispatchEvent(new Event('change'));
    fixture.detectChanges();
  }

  /**
   * Helper function to process add account form
   */
  function processAddForm() {
    // Get all inputs fields
    const addEmailInput: HTMLInputElement = fixture.debugElement.query(By.css('#add-account-email-input')).nativeElement;
    const addPasswordInput: HTMLInputElement = fixture.debugElement.query(By.css('#add-account-password-input')).nativeElement;
    const addAnswerInput: HTMLInputElement = fixture.debugElement.query(By.css('#add-account-answer-input')).nativeElement;
    const addKeyInput: HTMLInputElement = fixture.debugElement.query(By.css('#add-account-key-input')).nativeElement;

    testAccount.platform = Platform.XboxOne;
    const platformType = '4: XboxOne'; // 4 is the index if the dropdown.

    const modalAddButton: HTMLElement = fixture.debugElement.query(By.css('#add-account-modal-btn')).nativeElement;

    const formSubmit = fixture.debugElement.query(By.css('#add-account-form-btn')).nativeElement;
    const formSubmitDisabled = formSubmit.getAttribute('disabled');

    expect(modalAddButton).toBeDefined();
    modalAddButton.click();
    fixture.detectChanges();

    expect(formSubmitDisabled).toBeDefined();
    // Input data.
    addEmailInput.value = 'testinvalidemail';
    addPasswordInput.value = testAccount.password;
    addAnswerInput.value = testAccount.answer;
    addKeyInput.value = testAccount.secretKey;

    addEmailInput.dispatchEvent(new Event('input'));
    addPasswordInput.dispatchEvent(new Event('input'));
    addAnswerInput.dispatchEvent(new Event('input'));
    addKeyInput.dispatchEvent(new Event('input'));
    clickOnMultiselect(platformType, 'add-account-platform-input');

    fixture.detectChanges();

    expect(component.accountAddForm.valid).toBe(false);
    expect(formSubmitDisabled).toBeDefined();

    addEmailInput.value = testAccount.email;
    addEmailInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(component.accountAddForm.valid).toBe(true);
    expect(formSubmitDisabled).toBe('');

    expect(component.accountAddForm.get('email').value).toBe(testAccount.email);
    expect(component.accountAddForm.get('password').value).toBe(testAccount.password);
    expect(component.accountAddForm.get('answer').value).toBe(testAccount.answer);
    expect(component.accountAddForm.get('secretKey').value).toBe(testAccount.secretKey);
    expect(component.accountAddForm.get('platform').value).toBe(Platform[testAccount.platform]);
  }

  /**
   * Helper function to process update account form
   */
  function processUpdateForm() {
    const formSubmit = fixture.debugElement.query(By.css('#update-account-form-btn')).nativeElement;
    const formSubmitDisabled = formSubmit.getAttribute('disabled');

    expect(component.accountUpdateForm.valid).toBe(true);

    expect(component.accountUpdateForm.get('email').value).toBe(testAccount.email);
    expect(component.accountUpdateForm.get('password').value).toBe(testAccount.password);
    expect(component.accountUpdateForm.get('answer').value).toBe(testAccount.answer);
    expect(component.accountUpdateForm.get('secretKey').value).toBe(testAccount.secretKey);
    expect(component.accountUpdateForm.get('platform').value).toBe(Platform[Platform[testAccount.platform]]);

    // Get all inputs fields
    const updateEmailInput: HTMLInputElement = fixture.debugElement.query(By.css('#update-account-email-input')).nativeElement;
    expect(formSubmitDisabled).toBeDefined();

    // Input data.
    updateEmailInput.value = 'testinvalidemail';

    updateEmailInput.dispatchEvent(new Event('input'));

    fixture.detectChanges();

    expect(component.accountUpdateForm.valid).toBe(false);
    expect(formSubmitDisabled).toBeDefined();

    const updatedEmail = testAccount.email + '.org';
    updateEmailInput.value = updatedEmail;
    updateEmailInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(component.accountUpdateForm.valid).toBe(true);

    expect(component.accountUpdateForm.get('email').value).toBe(updatedEmail);
    expect(component.accountUpdateForm.get('password').value).toBe(testAccount.password);
    expect(component.accountUpdateForm.get('answer').value).toBe(testAccount.answer);
    expect(component.accountUpdateForm.get('secretKey').value).toBe(testAccount.secretKey);
    expect(component.accountUpdateForm.get('platform').value).toBe(Platform[Platform[testAccount.platform]]);
  }

  /**
   * Verify component created
   * Verify accounts list
   * Verify account count
   */
  it('should be created', async () => {
    // const startSpy = spyOnWsConnect
    injectAccount();
    component.getAccounts();
    fixture.detectChanges();
    getTable();
    getRows();

    const accountLength = fixture.debugElement.query(By.css('#accounts-length')).nativeElement;

    expect(component).toBeTruthy();
    expect(component.accounts).toBeTruthy();
    expect(component.accounts.length).toBeGreaterThan(0);
    expect(accountTable.nativeElement).toBeTruthy();
    expect(accountLength.textContent).toContain(component.accounts.length);

    tableRows.forEach(row => {
      const rowElement = row.nativeElement;
      expect(rowElement).toBeTruthy();
      expect(rowElement.textContent).toContain(accounts[0].email);
    });
  });

  /**
   * Verify no accounts.
   * Verify message displayed.
   */
  it('should display message when no accounts are available to be displayed.', () => {
    component.accounts = [];
    accounts = [];
    fixture.detectChanges();

    const noAccountsMessage = fixture.debugElement.query(By.css('#no-accounts-msg'));
    expect(component).toBeTruthy();
    expect(component.accounts).toBeTruthy();
    expect(component.accounts.length).toBe(0);
    expect(noAccountsMessage.nativeElement).toBeTruthy();
    expect(noAccountsMessage.nativeElement.textContent).toContain(NO_ACCOUNTS_MESSAGE);
  });

  /**
   * Verify add account element exists
   * Verify on add button click, modal pops up.
   * Verify modal can be closed.
   * Verify add can be added via form.
   */
  it('should add an account successfully.', fakeAsync(() => {
    processAddForm();
    const successMsg = 'Account added successfully.';
    // Submit the form
    const accountAddSpy = spyOn(accountService, 'addAccount').and.returnValue(of(testAccount));
    injectAccount();
    component.onAddForm();
    fixture.detectChanges();
    expect(component.accountsMsg).toContain(successMsg);
    tick(4000);

    expect(accountAddSpy).toHaveBeenCalled();

    // Verify form reset
    expect(component.accountAddForm.get('email').value).toBe('');
    expect(component.accountAddForm.get('password').value).toBe('');
    expect(component.accountAddForm.get('answer').value).toBe('');
    expect(component.accountAddForm.get('secretKey').value).toBe('');
    expect(component.accountAddForm.get('platform').value).toBe('');

    // Verify new account on list
    expect(component.accounts).toContain(testAccount);
  }));

  /**
   * Verify account cannot be added.
   * Verify form values retained.
   * Verify error message displayed.
   */
  it('should fail to add an account.', fakeAsync(() => {
    processAddForm();
    const failedMsg = 'Error adding';
    // Submit the form
    const accountAddSpy = spyOn(accountService, 'addAccount').and.returnValue(throwError(
      {
        error: {
          reason: 'Account already exists.',
          code: 500,
          message: 'Account already exists',
          debug: '',
          string: ''
        }

      }));
    component.onAddForm();
    fixture.detectChanges();

    expect(accountAddSpy).toHaveBeenCalled();

    // Verify form not reset
    expect(component.accountAddForm.get('email').value).toBe(testAccount.email);
    expect(component.accountAddForm.get('password').value).toBe(testAccount.password);
    expect(component.accountAddForm.get('answer').value).toBe(testAccount.answer);
    expect(component.accountAddForm.get('secretKey').value).toBe(testAccount.secretKey);
    expect(component.accountAddForm.get('platform').value).toBe(Platform[testAccount.platform]);

    // Verify error message displayed
    const errorMsg = fixture.debugElement.query(By.css('#add-form-error-msg'));
    expect(component.accountAddFormMsg).toContain(failedMsg);
    expect(errorMsg.nativeElement.textContent).toContain(failedMsg);
  }));

  /**
   * Verify account can be view in modal.
   * Verify account updated successfully.
   *
   */
  it('should view an account and update an account successfully', fakeAsync(() => {
    injectAccount();
    spyOn(globalServiceMock, 'getPlatform').and.returnValue(testAccount.platform);
    const spyRowClick = spyOn(component, 'rowClick').and.callThrough();
    const successMsg = 'Account updated successfully.';
    // Submit the form
    const updatedAccount = testAccount;
    updatedAccount.email = testAccount.email + '.org';

    const accountUpdateSpy = spyOn(accountService, 'updateAccount').and.returnValue(of(updatedAccount));
    component.createForm();
    component.getAccounts();
    fixture.detectChanges();

    // Click on table row
    getTable();
    getRows();

    tableRows.forEach(row => {
      const rowElement = row.nativeElement;
      if (rowElement.textContent.indexOf(testAccount.email) >= 0) {
        rowElement.click();
        fixture.detectChanges();
      }
    });

    processUpdateForm();

    component.onUpdateForm();
    fixture.detectChanges();
    expect(component.accountsMsg).toContain(successMsg);
    tick(4000);

    // Verify rowCLick was called.
    expect(spyRowClick).toHaveBeenCalled();
    expect(accountUpdateSpy).toHaveBeenCalled();

    expect(component.accounts[0].email).toBe(updatedAccount.email);
  }));

  /**
   * Verify it connects to the web socket.
   */
  it('should connect to the websocket', () => {
    injectAccount();
    sendMessage();
    component.startWs();
    fixture.detectChanges();
    expect(spyOnWsConnect).toHaveBeenCalled();
    expect(component.status.length).toBeGreaterThan(0);
  });

  /**
   * Verify error thrown when unable to get accounts
   */
  it('should throw an error when retrieving the accounts', () => {
    const accountServiceSpy = spyOn(accountService, 'getAccounts').and
      .returnValue(throwError(
        {
          error: {
            reason: 'Server error.',
            code: 500,
            message: 'Server error.',
            debug: '',
            string: ''
          }

        })
      );
    component.getAccounts();

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(accountServiceSpy).toHaveBeenCalled();
      expect(component.error).toBeTruthy();
      expect(component.error).toContain('Error getting all accounts');
    });
  });
});
