import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {UnassignedComponent} from './unassigned.component';
import {LoaderComponent} from '../../loader/loader.component';
import {UnassignedService} from './unassigned.service';
import {AppRoutingModule} from '../../app-routing.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CookieModule, CookieService} from 'ngx-cookie';
import {AlertModule, AlertService} from '../../alert';
import {BrowserModule} from '@angular/platform-browser';
import {MultiselectDropdownModule} from 'angular-2-dropdown-multiselect';
import {NgxPaginationModule} from 'ngx-pagination';
import {LoginComponent} from '../../login/login.component';
import {AccountsComponent} from '../accounts/accounts.component';
import {PlayersMgmtComponent} from '../players-mgmt/players-mgmt.component';
import {StatusModalComponent} from '../../status-modal/status-modal.component';
import {PlayerPriceComponent} from '../players-mgmt/player-price/player-price.component';
import {PlayerSearchPipe} from '../../pipe/player-search.pipe';
import {TableSearchPipe} from '../../pipe/table-search.pipe';
import {OrderByPipe} from '../../pipe/orderByPipe';
import {MainComponent} from '../main.component';
import {PlayerListComponent} from '../players-mgmt/player-list/players-list.component';
import {PlayerFilterComponent} from '../../player-filter/player-filter.component';
import {PlayerCardComponent} from '../player-card/player-card.component';
import {InputFiltersComponent} from '../../input-filters/input-filters.component';
import {SidebarComponent} from '../../sidebar/sidebar.component';
import {TradePileComponent} from '../trade-pile/trade-pile.component';
import {WatchListComponent} from '../watch-list/watch-list.component';
import {TransferSearchComponent} from '../transfer-search/transfer-search.component';
import {AutoBidComponent} from '../auto-bid/auto-bid.component';
import {APP_BASE_HREF} from '@angular/common';
import {GlobalService} from '../../globals/global.service';
import {AuthService} from '../../services/auth.service';
import {WatchListService} from '../watch-list/watch-list.service';
import {CacheService} from '../../services/cache.service';
import {AccountsService} from '../accounts/accounts.service';
import {PlayerPricesService} from '../players-mgmt/player-price/player-prices.service';
import {TradePileService} from '../trade-pile/trade-pile.service';
import {PlayerListService} from '../players-mgmt/player-list/player-list.service';
import {LogicService} from '../../services/logic.service';
import {LoginGuard} from '../../guards/login-guard.service';
import {StateService} from '../../status-modal/state-service';
import {AutoBidService} from '../auto-bid/auto-bid.service';
import {SearchService} from '../transfer-search/search.service';
import {AccountTestUtils} from '../../../test-utils/account.test.utils';
import {Account} from '../../domain/account';

xdescribe('UnassignedComponent', () => {
  let component: UnassignedComponent;
  let fixture: ComponentFixture<UnassignedComponent>;
  let globalService: GlobalService;
  let testAccount: Account;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        LoaderComponent,
        SidebarComponent,
        AccountsComponent,
        LoginComponent,
        MainComponent,
        TradePileComponent,
        AutoBidComponent,
        WatchListComponent,
        PlayerListComponent,
        PlayerPriceComponent,
        PlayersMgmtComponent,
        InputFiltersComponent,
        OrderByPipe,
        TableSearchPipe,
        PlayerSearchPipe,
        PlayerFilterComponent,
        PlayerCardComponent,
        TransferSearchComponent,
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
        HttpClientXsrfModule,
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
        UnassignedService,
        {provide: APP_BASE_HREF, useValue: '/'}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnassignedComponent);
    globalService = TestBed.get(GlobalService);

    fixture.detectChanges();
    testAccount = AccountTestUtils.createMockAccount();
    injectAccount();
    globalService.loggedIn = true;
    globalService.currentAccount = testAccount;

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /**
   * Helper function to inject account
   */
  function injectAccount() {
    const accounts: Account[] = [];
    accounts.push(testAccount);
    globalService.newAccount(accounts);
    fixture.detectChanges();
  }


  it('should create', () => {
    fixture.whenStable().then(() => {
      testAccount = AccountTestUtils.createMockAccount();
      injectAccount();
      globalService.loggedIn = true;
      /* globalService.onNewAccount.forEach(a => {
         a.forEach(acc => {
           if (acc.id === testAccount.id) {
             globalService.currentAccount = acc;
             console.log(globalService.currentAccount);
           }
         });
       });*/
      globalService.currentAccount = testAccount;
      fixture.detectChanges();
      expect(component).toBeTruthy();
    });
  });
});
