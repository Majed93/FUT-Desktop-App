import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {LoginGuard} from './guards/login-guard.service';
import {AccountsComponent} from './main/accounts/accounts.component';
import {MainComponent} from './main/main.component';
import {TradePileComponent} from './main/trade-pile/trade-pile.component';
import {PlayersMgmtComponent} from './main/players-mgmt/players-mgmt.component';
import {WatchListComponent} from './main/watch-list/watch-list.component';
import {TransferSearchComponent} from './main/transfer-search/transfer-search.component';
import {AutoBidComponent} from './main/auto-bid/auto-bid.component';
import {UnassignedComponent} from './main/unassigned/unassigned.component';

const routes: Routes = [
  {
    path: '', redirectTo: '/login', pathMatch: 'full'
  },
  {
    path: '', redirectTo: 'main/accounts', pathMatch: 'full'
  },
  {
    path: 'login', component: LoginComponent
  },
  {
    path: 'main', component: MainComponent, canActivate: [LoginGuard],
    children: [
      {
        path: 'accounts/:auth', component: AccountsComponent
      },
      {
        path: 'accounts', component: AccountsComponent
      },
      {
        path: 'trade-pile', component: TradePileComponent
      },
      {
        path: 'watch-list', component: WatchListComponent
      },
      {
        path: 'unassigned', component: UnassignedComponent
      },
      {
        path: 'transfer-search', component: TransferSearchComponent
      },
      {
        path: 'auto-bid', component: AutoBidComponent
      },
      {
        path: 'players-list', component: PlayersMgmtComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes/*, {enableTracing: true}*/)],
  exports: [RouterModule]
})

export class AppRoutingModule {
}
