import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AppComponent} from './app.component';
import {LoaderComponent} from './loader/loader.component';
import {MainComponent} from './main/main.component';
import {LoginComponent} from './login/login.component';
import {PlayerListService} from './main/players-mgmt/player-list/player-list.service';
import {GlobalService} from './globals/global.service';
import {AutoBidService} from './main/auto-bid/auto-bid.service';
import {SearchService} from './main/transfer-search/search.service';
import {PlayerPricesService} from './main/players-mgmt/player-price/player-prices.service';
import {LoginGuard} from './guards/login-guard.service';
import {CacheService} from './services/cache.service';
import {LogicService} from './services/logic.service';
import {WatchListService} from './main/watch-list/watch-list.service';
import {TradePileService} from './main/trade-pile/trade-pile.service';
import {AccountsService} from './main/accounts/accounts.service';
import {StateService} from './status-modal/state-service';
import {CookieModule, CookieService} from 'ngx-cookie';
import {AuthService} from './services/auth.service';
import {HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {AlertModule} from './alert/module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppRoutingModule} from './app-routing.module';
import {NgxPaginationModule} from 'ngx-pagination';
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MultiselectDropdownModule} from 'angular-2-dropdown-multiselect';
import {OrderByPipe} from './pipe/orderByPipe';
import {AutoBidComponent} from './main/auto-bid/auto-bid.component';
import {PlayersMgmtComponent} from './main/players-mgmt/players-mgmt.component';
import {PlayerPriceComponent} from './main/players-mgmt/player-price/player-price.component';
import {AccountsComponent} from './main/accounts/accounts.component';
import {PlayerCardComponent} from './main/player-card/player-card.component';
import {TransferSearchComponent} from './main/transfer-search/transfer-search.component';
import {TradePileComponent} from './main/trade-pile/trade-pile.component';
import {SidebarComponent} from './sidebar/sidebar.component';
import {PlayerListComponent} from './main/players-mgmt/player-list/players-list.component';
import {WatchListComponent} from './main/watch-list/watch-list.component';
import {StatusModalComponent} from './status-modal/status-modal.component';
import {APP_BASE_HREF} from '@angular/common';
import {RouterTestingModule} from '@angular/router/testing';
import {Router} from '@angular/router';
import {TableSearchPipe} from './pipe/table-search.pipe';
import {InputFiltersComponent} from './input-filters/input-filters.component';
import {PlayerFilterComponent} from './player-filter/player-filter.component';
import {PlayerSearchPipe} from './pipe/player-search.pipe';
import {UnassignedComponent} from './main/unassigned/unassigned.component';
import {UnassignedService} from './main/unassigned/unassigned.service';
import {ActionNeededComponent} from './action-needed/action-needed.component';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';

describe('AppComponent', () => {
  let app: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let router: Router;

  /**
   * Runs before each test.
   */
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
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
        PlayerCardComponent,
        WatchListComponent,
        TransferSearchComponent,
        InputFiltersComponent,
        AutoBidComponent,
        StatusModalComponent,
        UnassignedComponent,
        ActionNeededComponent
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
        HttpClientXsrfModule,
        RouterTestingModule.withRoutes([]),
        AlertModule,
        CookieModule.forChild()
      ], providers: [
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
        UnassignedService,
        {provide: APP_BASE_HREF, useValue: '/'}
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    router = TestBed.get(Router);

    fixture = TestBed.createComponent(AppComponent);
    app = fixture.componentInstance;
    router.initialNavigation();
    app.loading = false;
    fixture.detectChanges();
  });

  it('should create the app', async(() => {
    expect(app).toBeTruthy();
  }));
});
