import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AutoBidComponent} from './auto-bid.component';
import {MultiselectDropdownModule} from 'angular-2-dropdown-multiselect';
import {OrderByPipe} from '../../pipe/orderByPipe';
import {MainComponent} from '../main.component';
import {PlayersMgmtComponent} from '../players-mgmt/players-mgmt.component';
import {PlayerPriceComponent} from '../players-mgmt/player-price/player-price.component';
import {AccountsComponent} from '../accounts/accounts.component';
import {PlayerCardComponent} from '../player-card/player-card.component';
import {TransferSearchComponent} from '../transfer-search/transfer-search.component';
import {LoginComponent} from '../../login/login.component';
import {TradePileComponent} from '../trade-pile/trade-pile.component';
import {LoaderComponent} from '../../loader/loader.component';
import {SidebarComponent} from '../../sidebar/sidebar.component';
import {PlayerListComponent} from '../players-mgmt/player-list/players-list.component';
import {WatchListComponent} from '../watch-list/watch-list.component';
import {StatusModalComponent} from '../../status-modal/status-modal.component';
import {HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {AlertModule} from '../../alert/module';
import {CookieModule, CookieService} from 'ngx-cookie';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppRoutingModule} from '../../app-routing.module';
import {NgxPaginationModule} from 'ngx-pagination';
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {PlayerListService} from '../players-mgmt/player-list/player-list.service';
import {GlobalService} from '../../globals/global.service';
import {AutoBidService} from './auto-bid.service';
import {SearchService} from '../transfer-search/search.service';
import {PlayerPricesService} from '../players-mgmt/player-price/player-prices.service';
import {LoginGuard} from '../../guards/login-guard.service';
import {CacheService} from '../../services/cache.service';
import {APP_BASE_HREF} from '@angular/common';
import {LogicService} from '../../services/logic.service';
import {WatchListService} from '../watch-list/watch-list.service';
import {TradePileService} from '../trade-pile/trade-pile.service';
import {AccountsService} from '../accounts/accounts.service';
import {StateService} from '../../status-modal/state-service';
import {AuthService} from '../../services/auth.service';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {BidCriteriaComponent} from './bid-criteria/bid-criteria.component';

xdescribe('AutoBidComponent', () => {
  let component: AutoBidComponent;
  let fixture: ComponentFixture<AutoBidComponent>;

  beforeEach(async(() => {
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
        PlayerCardComponent,
        WatchListComponent,
        TransferSearchComponent,
        AutoBidComponent,
        StatusModalComponent,
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
        HttpClientXsrfModule,
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
        {provide: APP_BASE_HREF, useValue: '/'}],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AutoBidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
