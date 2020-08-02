import {GlobalService} from '../globals/global.service';
import {TestBed} from '@angular/core/testing';
import {AuthService} from '../services/auth.service';
import {LoginGuard} from './login-guard.service';
import {AppRoutingModule} from '../app-routing.module';
import {LoginComponent} from '../login/login.component';
import {MainComponent} from '../main/main.component';
import {AccountsComponent} from '../main/accounts/accounts.component';
import {WatchListComponent} from '../main/watch-list/watch-list.component';
import {SidebarComponent} from '../sidebar/sidebar.component';
import {PlayerCardComponent} from '../main/player-card/player-card.component';
import {TransferSearchComponent} from '../main/transfer-search/transfer-search.component';
import {PlayerListComponent} from '../main/players-mgmt/player-list/players-list.component';
import {TradePileComponent} from '../main/trade-pile/trade-pile.component';
import {OrderByPipe} from '../pipe/orderByPipe';
import {PlayersMgmtComponent} from '../main/players-mgmt/players-mgmt.component';
import {StatusModalComponent} from '../status-modal/status-modal.component';
import {LoaderComponent} from '../loader/loader.component';
import {PlayerPriceComponent} from '../main/players-mgmt/player-price/player-price.component';
import {AutoBidComponent} from '../main/auto-bid/auto-bid.component';
import {MultiselectDropdownModule} from 'angular-2-dropdown-multiselect';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CookieModule, CookieService} from 'ngx-cookie';
import {HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {AlertModule} from '../alert/module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgxPaginationModule} from 'ngx-pagination';
import {StateService} from '../status-modal/state-service';
import {LogicService} from '../services/logic.service';
import {PlayerListService} from '../main/players-mgmt/player-list/player-list.service';
import {AutoBidService} from '../main/auto-bid/auto-bid.service';
import {APP_BASE_HREF} from '@angular/common';
import {CacheService} from '../services/cache.service';
import {SearchService} from '../main/transfer-search/search.service';
import {AccountsService} from '../main/accounts/accounts.service';
import {PlayerPricesService} from '../main/players-mgmt/player-price/player-prices.service';
import {TradePileService} from '../main/trade-pile/trade-pile.service';
import {WatchListService} from '../main/watch-list/watch-list.service';
import {BrowserModule} from '@angular/platform-browser';
import {TableSearchPipe} from '../pipe/table-search.pipe';
import {InputFiltersComponent} from '../input-filters/input-filters.component';
import {PlayerFilterComponent} from '../player-filter/player-filter.component';
import {PlayerSearchPipe} from '../pipe/player-search.pipe';
import {UnassignedComponent} from '../main/unassigned/unassigned.component';
import {ActionNeededComponent} from '../action-needed/action-needed.component';
import {BidCriteriaComponent} from '../main/auto-bid/bid-criteria/bid-criteria.component';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';

describe('LoginGuardService', () => {
  let guard: LoginGuard;
  let authService: AuthService;

  /**
   * Run before each test
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
        OrderByPipe,
        TableSearchPipe,
        PlayerSearchPipe,
        PlayerFilterComponent,
        InputFiltersComponent,
        PlayerCardComponent,
        WatchListComponent,
        TransferSearchComponent,
        AutoBidComponent,
        StatusModalComponent,
        ActionNeededComponent,
        UnassignedComponent,
        BidCriteriaComponent
      ],
      imports: [
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
        AlertModule,
        CookieModule.forChild()
      ],
      providers: [
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
        {provide: APP_BASE_HREF, useValue: '/'}
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents();
  });

  /**
   * Run before each test
   */
  beforeEach(() => {
    guard = TestBed.get(LoginGuard);
    authService = TestBed.get(AuthService);
  });

  /**
   * Verify guard is created.
   */
  it('should create guard', () => {
    expect(guard).toBeTruthy();
  });

  /**
   * Verify true is returned when authorised.
   */
  it('should create guard', () => {
    spyOn(authService, 'isAuthorised').and.returnValue(true);
    const result = guard.canActivate();
    expect(guard).toBeTruthy();
    expect(result).toBeTruthy();
  });

  /**
   * Verify false is returned when not authorised.
   */
  it('should create guard', () => {
    spyOn(authService, 'isAuthorised').and.returnValue(false);
    const result = guard.canActivate();
    expect(guard).toBeTruthy();
    expect(result).toBeFalsy();
  });
});
