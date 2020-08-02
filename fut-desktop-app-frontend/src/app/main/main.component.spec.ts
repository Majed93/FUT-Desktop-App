import {ComponentFixture, TestBed} from '@angular/core/testing';

import {MainComponent} from './main.component';
import {BrowserModule, By} from '@angular/platform-browser';
import {SidebarComponent} from '../sidebar/sidebar.component';
import {PlayerCardComponent} from './player-card/player-card.component';
import {CookieModule, CookieService} from 'ngx-cookie';
import {PlayerListComponent} from './players-mgmt/player-list/players-list.component';
import {PlayerListService} from './players-mgmt/player-list/player-list.service';
import {AutoBidService} from './auto-bid/auto-bid.service';
import {APP_BASE_HREF} from '@angular/common';
import {AccountsComponent} from './accounts/accounts.component';
import {CacheService} from '../services/cache.service';
import {SearchService} from './transfer-search/search.service';
import {OrderByPipe} from '../pipe/orderByPipe';
import {PlayersMgmtComponent} from './players-mgmt/players-mgmt.component';
import {GlobalService} from '../globals/global.service';
import {StatusModalComponent} from '../status-modal/status-modal.component';
import {AuthService} from '../services/auth.service';
import {HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {LoaderComponent} from '../loader/loader.component';
import {AutoBidComponent} from './auto-bid/auto-bid.component';
import {WatchListComponent} from './watch-list/watch-list.component';
import {MultiselectDropdownModule} from 'angular-2-dropdown-multiselect';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {StateService} from '../status-modal/state-service';
import {TransferSearchComponent} from './transfer-search/transfer-search.component';
import {LogicService} from '../services/logic.service';
import {LoginComponent} from '../login/login.component';
import {TradePileComponent} from './trade-pile/trade-pile.component';
import {NgxPaginationModule} from 'ngx-pagination';
import {LoginGuard} from '../guards/login-guard.service';
import {PlayerPricesService} from './players-mgmt/player-price/player-prices.service';
import {TradePileService} from './trade-pile/trade-pile.service';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {PlayerPriceComponent} from './players-mgmt/player-price/player-price.component';
import {WatchListService} from './watch-list/watch-list.service';
import {AppRoutingModule} from '../app-routing.module';
import {Account} from '../domain/account';
import {AlertModule, AlertService} from '../alert';
import {AccountsService} from './accounts/accounts.service';
import {AccountTestUtils} from '../../test-utils/account.test.utils';
import {TableSearchPipe} from '../pipe/table-search.pipe';
import {InputFiltersComponent} from '../input-filters/input-filters.component';
import {PlayerFilterComponent} from '../player-filter/player-filter.component';
import {PlayerSearchPipe} from '../pipe/player-search.pipe';
import {UnassignedComponent} from './unassigned/unassigned.component';
import {of, throwError} from 'rxjs';
import {ActionNeededComponent} from '../action-needed/action-needed.component';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {ActionNeededService} from '../action-needed/action-needed.service';

describe('MainComponent', () => {
  let component: MainComponent;
  let fixture: ComponentFixture<MainComponent>;
  let globalService: GlobalService;
  let loginService: LogicService;
  let testAccount: Account;

  const LOGGED_IN_ID = 'current-logged-in';
  const ACCOUNT_LIST_ID = 'accounts-select';
  const COINS_ID = 'total-coins';
  const CURRENT_COINS_ID = 'current-account-coins';

  /** JSON Test files */
  const mockLogin: any = require('../../test-assets/login.response.json');
  const allPlayersJson: any = require('../../test-assets/test.players.json');
  const allClubsJson: any = require('../../test-assets/test.clubs.json');
  const allNationsJson: any = require('../../test-assets/test.nations.json');
  const allLeaguesJson: any = require('../../test-assets/test.leagues.json');
  const allPositionsJson: any = require('../../test-assets/test.positions.json');

  /**
   * Before each test
   */
  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [
        LoaderComponent,
        SidebarComponent,
        AccountsComponent,
        LoginComponent,
        MainComponent,
        TradePileComponent,
        PlayerListComponent,
        PlayerPriceComponent,
        PlayersMgmtComponent,
        InputFiltersComponent,
        OrderByPipe,
        TableSearchPipe,
        PlayerSearchPipe,
        PlayerFilterComponent,
        PlayerCardComponent,
        WatchListComponent,
        TransferSearchComponent,
        AutoBidComponent,
        StatusModalComponent,
        UnassignedComponent,
        ActionNeededComponent
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
        HttpClientXsrfModule.withOptions({
          cookieName: 'XSRF-TOKEN',
          headerName: 'XSRF-TOKEN'
        }),
        CookieModule.forChild()
      ],
      providers: [
        AlertService,
        AccountsService,
        CookieService,
        CacheService,
        AuthService,
        LogicService,
        TradePileService,
        WatchListService,
        PlayerListService,
        PlayerPricesService,
        AutoBidService,
        GlobalService,
        StateService,
        SearchService,
        LoginGuard,
        ActionNeededService,
        {provide: APP_BASE_HREF, useValue: '/'}],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents();
  });

  /**
   * Before each test
   */
  beforeEach(() => {
    fixture = TestBed.createComponent(MainComponent);
    globalService = TestBed.get(GlobalService);
    loginService = TestBed.get(LogicService);
    // Setup mock account
    testAccount = AccountTestUtils.createMockAccount();
    getJsonResources();
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /**
   * Helper function to get players json
   */
  function getJsonResources() {
    globalService.isPlayersLoaded = true;
    globalService.isPlayerResourcesLoaded = true;
    globalService.allPlayers = allPlayersJson;
    globalService.allTeams = allClubsJson;
    globalService.allNations = allNationsJson;
    globalService.allLeagues = allLeaguesJson;
    globalService.allPositions = allPositionsJson;
    fixture.detectChanges();
  }

  /**
   * Helper function to inject account
   */
  function injectAccount() {
    const accounts: Account[] = [];
    accounts.push(testAccount);
    globalService.newAccount(accounts);
    fixture.detectChanges();
  }

  /**
   * Verify component created successfully.
   * Verify sidebar created.
   */
  it('should be created', () => {
    const sidebarElement = fixture.debugElement.query(By.css('app-sidebar'));
    const loggedIn = fixture.debugElement.query(By.css('#' + LOGGED_IN_ID));
    const accountList = fixture.debugElement.query(By.css('#' + ACCOUNT_LIST_ID));
    const coinsId = fixture.debugElement.query(By.css('#' + COINS_ID));
    const currentCoins = fixture.debugElement.query(By.css('#' + CURRENT_COINS_ID));

    injectAccount();

    const spyOnGetAllPlayers = spyOn(globalService, 'getAllPlayersJson').and.returnValue(of(allPlayersJson));
    const spyOnGetAllClubs = spyOn(globalService, 'getAllTeams').and.returnValue(of(allClubsJson));
    const spyOnGetAllNations = spyOn(globalService, 'getAllNations').and.returnValue(of(allNationsJson));
    const spyOnGetAllLeagues = spyOn(globalService, 'getAllLeagues').and.returnValue(of(allLeaguesJson));
    const spyOnGetAllPositions = spyOn(globalService, 'getPositions').and.returnValue(of(allPositionsJson));

    component.ngOnInit();
    fixture.detectChanges();

    expect(spyOnGetAllPlayers).toHaveBeenCalled();
    expect(spyOnGetAllClubs).toHaveBeenCalled();
    expect(spyOnGetAllNations).toHaveBeenCalled();
    expect(spyOnGetAllLeagues).toHaveBeenCalled();
    expect(spyOnGetAllPositions).toHaveBeenCalled();

    expect(component).toBeTruthy();
    expect(component.loading).toBe(false);
    expect(component.accountsList.length).toBe(1);

    expect(sidebarElement.nativeElement).toBeTruthy();
    expect(loggedIn).toBeFalsy();
    expect(accountList.nativeElement).toBeTruthy();
    expect(coinsId.nativeElement).toBeTruthy();
    expect(currentCoins).toBeFalsy();
  });

  /**
   * Verify component created successfully.
   * Verify sidebar created.
   * Verify logged in account test successfully.
   * Verify coins displayed.
   */
  it('should be able login to an account successfully.', () => {
    const sidebarElement = fixture.debugElement.query(By.css('app-sidebar'));
    const loggedIn = fixture.debugElement.query(By.css('#' + LOGGED_IN_ID));
    const accountList = fixture.debugElement.query(By.css('#' + ACCOUNT_LIST_ID));
    const coinsId = fixture.debugElement.query(By.css('#' + COINS_ID));
    const currentCoins = fixture.debugElement.query(By.css('#' + CURRENT_COINS_ID));

    injectAccount();
    expect(component).toBeTruthy();
    expect(component.loading).toBe(false);
    expect(component.accountsList.length).toBe(1);

    expect(sidebarElement.nativeElement).toBeTruthy();
    expect(loggedIn).toBeFalsy();
    expect(accountList.nativeElement).toBeTruthy();
    expect(coinsId.nativeElement).toBeTruthy();
    expect(currentCoins).toBeFalsy();

    const spyLogin = spyOn(loginService, 'login').and.returnValue(of(mockLogin));
    const updateCoinsSpy = spyOn(component, 'updateCoins');
    const currentAccountCoins = spyOn(component, 'currentAccountCoins');
    // Select the only account
    component.accountLogin.setValue(testAccount.id);
    // Perform login now
    fixture.detectChanges();
    component.login();

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(spyLogin).toHaveBeenCalled();
      expect(updateCoinsSpy).toHaveBeenCalled();
      expect(currentAccountCoins).toHaveBeenCalled();
      expect(loggedIn.nativeElement).toBeTruthy();
      expect(currentCoins.nativeElement).toBeTruthy();
      expect(currentCoins.nativeElement.textContent).not.toBe('');
      expect(coinsId.nativeElement.textContent).not.toBe('');
      expect(component.loggedIn).toBeTruthy();
      expect(loggedIn.nativeElement.textContent).toContain(testAccount.email);
    });
  });

  /**
   * Verify component created successfully.
   * Verify sidebar created.
   * Verify logged in account test unsuccessfully.
   * Verify coins displayed.
   */
  it('should be able login to an account unsuccessfully.', () => {
    const sidebarElement = fixture.debugElement.query(By.css('app-sidebar'));
    const loggedIn = fixture.debugElement.query(By.css('#' + LOGGED_IN_ID));
    const accountList = fixture.debugElement.query(By.css('#' + ACCOUNT_LIST_ID));
    const coinsId = fixture.debugElement.query(By.css('#' + COINS_ID));
    const currentCoins = fixture.debugElement.query(By.css('#' + CURRENT_COINS_ID));
    const alertElement = fixture.debugElement.query(By.css('alert'));

    injectAccount();
    expect(component).toBeTruthy();
    expect(component.loading).toBe(false);
    expect(component.accountsList.length).toBe(1);

    expect(sidebarElement.nativeElement).toBeTruthy();
    expect(loggedIn).toBeFalsy();
    expect(accountList.nativeElement).toBeTruthy();
    expect(coinsId.nativeElement).toBeTruthy();
    expect(currentCoins).toBeFalsy();

    const spyLogin = spyOn(loginService, 'login').and.returnValue(throwError({message: 'Error logging in'}));
    const updateCoinsSpy = spyOn(component, 'updateCoins');
    const currentAccountCoins = spyOn(component, 'currentAccountCoins');

    // Select the only account
    component.accountLogin.setValue(testAccount.id);
    // Perform login now
    fixture.detectChanges();
    component.login();

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(alertElement.nativeElement).toBeTruthy();
      expect(spyLogin).toHaveBeenCalled();
      expect(updateCoinsSpy).not.toHaveBeenCalled();
      expect(currentAccountCoins).not.toHaveBeenCalled();
      expect(loggedIn.nativeElement).toBeFalsy();
      expect(component.loggedIn).toBeFalsy();
    });
  });


  /**
   * Verify ng destroy.
   */
  it('should unsubscribe from the emitted when destroyed', () => {
    const spyOnUnsubscribe = spyOn(globalService.onNewAccount, 'unsubscribe');

    component.ngOnDestroy();

    expect(spyOnUnsubscribe).toHaveBeenCalled();
  });

  /**
   * Verify current coins of account is returned.
   */
  it('should return the current coins of the account', () => {
    globalService.currentAccount = testAccount;
    fixture.detectChanges();
    const result = component.currentAccountCoins();
    expect(result).toBe(testAccount.coins);
  });
});
