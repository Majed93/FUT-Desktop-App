import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {LoaderComponent} from './loader/loader.component';
import {SidebarComponent} from './sidebar/sidebar.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AccountsComponent} from './main/accounts/accounts.component';
import {AppRoutingModule} from './app-routing.module';
import {AccountsService} from './main/accounts/accounts.service';
import {AuthService} from './services/auth.service';
import {HTTP_INTERCEPTORS, HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {NoopInterceptor} from './interceptor/NoopInterceptor';
import {CookieModule, CookieService} from 'ngx-cookie';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MainComponent} from './main/main.component';
import {LoginComponent} from './login/login.component';
import {LoginGuard} from './guards/login-guard.service';
import {GlobalService} from './globals/global.service';
import {NgxPaginationModule} from 'ngx-pagination';
import {AlertComponent} from './alert/alert.component';
import {AlertModule} from './alert/module';
import {LogicService} from './services/logic.service';
import {TradePileComponent} from './main/trade-pile/trade-pile.component';
import {PlayerListComponent} from './main/players-mgmt/player-list/players-list.component';
import {TradePileService} from './main/trade-pile/trade-pile.service';
import {PlayerListService} from './main/players-mgmt/player-list/player-list.service';
import {PlayerPricesService} from './main/players-mgmt/player-price/player-prices.service';
import {PlayerPriceComponent} from './main/players-mgmt/player-price/player-price.component';
import {PlayersMgmtComponent} from './main/players-mgmt/players-mgmt.component';
import {OrderByPipe} from './pipe/orderByPipe';
import {PlayerCardComponent} from './main/player-card/player-card.component';
import {WatchListComponent} from './main/watch-list/watch-list.component';
import {TransferSearchComponent} from './main/transfer-search/transfer-search.component';
import {AutoBidComponent} from './main/auto-bid/auto-bid.component';
import {StatusModalComponent} from './status-modal/status-modal.component';
import {StateService} from './status-modal/state-service';
import {WatchListService} from './main/watch-list/watch-list.service';
import {AutoBidService} from './main/auto-bid/auto-bid.service';
import {MultiselectDropdownModule} from 'angular-2-dropdown-multiselect';
import {SearchService} from './main/transfer-search/search.service';
import {CacheService} from './services/cache.service';
import {InputFiltersComponent} from './input-filters/input-filters.component';
import {TableSearchPipe} from './pipe/table-search.pipe';
import {PlayerFilterComponent} from './player-filter/player-filter.component';
import {PlayerSearchPipe} from './pipe/player-search.pipe';
import {UnassignedComponent} from './main/unassigned/unassigned.component';
import {UnassignedService} from './main/unassigned/unassigned.service';
import {WebSocketService} from './services/websocket.service';
import { ActionNeededComponent } from './action-needed/action-needed.component';
import {ActionNeededService} from './action-needed/action-needed.service';
import { BidCriteriaComponent } from './main/auto-bid/bid-criteria/bid-criteria.component';

@NgModule({
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
    AutoBidComponent,
    StatusModalComponent,
    InputFiltersComponent,
    PlayerFilterComponent,
    UnassignedComponent,
    ActionNeededComponent,
    BidCriteriaComponent
  ],
  entryComponents: [AlertComponent],
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
  providers: [AccountsService,
    ActionNeededService,
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
    {
      provide: HTTP_INTERCEPTORS,
      useClass: NoopInterceptor,
      multi: true
    },
    WebSocketService
  ],
  bootstrap: [AppComponent]
})

export class AppModule {
}
