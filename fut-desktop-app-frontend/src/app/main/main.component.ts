import {Component, HostListener, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Account} from '../domain/account';
import {GlobalService} from '../globals/global.service';
import {LogicService} from '../services/logic.service';
import {FormControl} from '@angular/forms';
import {AlertService} from '../alert';
import {Player} from '../domain/player';
import {SidebarComponent} from '../sidebar/sidebar.component';
import {Router} from '@angular/router';

/**
 * The main component
 */
@Component({
  moduleId: module.id,
  selector: 'app-main',
  templateUrl: 'main.component.html',
  styleUrls: ['main.component.scss']
})
export class MainComponent implements OnInit, OnDestroy {

  loggedIn: string; // put back in thing at bottom too.
  accountsList: Account[];
  accountLogin = new FormControl();
  appVersion = new FormControl();
  appVersions: Array<any> = [
    {id: 'WebApp', display: 'Web app'},
    {id: 'CompanionApp', display: 'Companion App'}];
  loginBtnDisabled = false;
  @Input() coins = 0;
  loading = true;

  @ViewChild('sideBar') sideBar: SidebarComponent;

  /** In seconds, how long to keep the user logged into their FUT account before auto logging out. */
  private readonly SESSION_EXPIRES = 900000; // 15minutes
  userActivity;

  constructor(public globals: GlobalService, private loginService: LogicService,
              private alerts: AlertService, private router: Router) {
    this.globals.onNewAccount.subscribe(resp => {
      this.accountsList = resp;
      if (!this.globals.currentAccount) {
        if (this.accountsList.length > 0) {
          this.accountLogin.setValue(this.accountsList[0].id);
        }
      }
    });

    this.appVersion.setValue(this.appVersions[0].id);
  }

  /**
   * Detect user inactivity when logged in and log them out.
   */
  @HostListener('document:keypress')
  @HostListener('window:mousemove') hostListener() {
    if (this.userActivity) {
      clearTimeout(this.userActivity);
    }
    if (this.globals.loggedIn) {
      this.beginLogoutTimer();
    }
  }

  /**
   * Begin timer to auto log out user after X minutes.
   */
  beginLogoutTimer(): void {
    this.userActivity = setTimeout(() => {
      this.router.navigate(['main/accounts']);
      this.globals.loggedIn = false;
      this.globals.currentAccount = null;
      this.loggedIn = '';
      this.alerts.show(`Logged out of EA account due to inactivity. Please login again.`, {cssClass: 'danger'});
      // this.onLoggedOut.emit(true);
      clearTimeout(this.userActivity);
    }, this.SESSION_EXPIRES);
  }

  ngOnInit() {
    this.globals.updateCoinCount();
    this.updateCoins();
    this.loading = false;

    // Get all players json.
    this.globals.getPlayerResourceData();
    this.globals.getAllPlayersJson().subscribe(players => {
      this.globals.isPlayersLoaded = true;
      this.sideBar.playersLoaded = true;
      const allPlayersArray: Player[] = [];
      players.forEach(p => {
        // Check to see if h
        const foundPlayer: Player = allPlayersArray.find(aap => aap.firstName === p.firstName &&
          aap.lastName === p.lastName &&
          p.assetId > aap.assetId);

        if (!foundPlayer) {
          const newPlayer: Player = new Player();
          newPlayer.firstName = p.firstName;
          newPlayer.lastName = p.lastName;
          newPlayer.commonName = p.commonName;
          newPlayer.assetId = p.assetId;
          newPlayer.nation = p.nation;
          newPlayer.rating = p.rating;

          allPlayersArray.push(newPlayer);
        }
      });

      this.globals.allPlayers = allPlayersArray;

    }, error => {
      console.error('Error getting all players json: ', error);
    });
  }

  /**
   * On component destroy
   */
  ngOnDestroy(): void {
    if (this.globals) {
      //  to avoid memory leaks
      this.globals.onNewAccount.unsubscribe();
    }

    if (this.userActivity) {
      clearTimeout(this.userActivity);
    }
  }

  /**
   * Login
   */
  login(): void {
    this.loading = true;
    this.loginBtnDisabled = true;
    this.loggedIn = '';
    this.loginService.login(this.accountLogin.value, this.appVersion.value).subscribe(resp => {
      if (resp !== null) {
        this.accountsList.forEach(a => {
          if (a.id === Number(this.accountLogin.value)) {
            if (resp.homeWrapper && resp.homeWrapper.tradePile) {
              a.coins = resp.homeWrapper.tradePile.credits;
            }
            this.loggedIn = 'Logged in as: ' + a.email;
            this.globals.currentAccount = a;
            this.sideBar.accountChange(a);
            this.currentAccountCoins();
            this.updateCoins();
          }
        });
        this.globals.loggedIn = true;
        this.loginBtnDisabled = false;
        this.loading = false;
      }
    }, error => {
      this.alerts.show('Unable to login. Check account details are correct and you can access the web app.', {cssClass: 'danger'});
      console.log('Error logging into app: ' + JSON.stringify(error));
      this.loginBtnDisabled = false;
      this.loading = false;
    });
  }

  /**
   * Get platform in user readable format.
   *
   * @param {string} platformKey
   * @returns {string}
   */
  getPlatform(platformKey: string): string {
    return this.globals.getPlatform(platformKey);
  }

  /**
   * Update on new accounts
   */
  updateCoins(): void {
    if (this.accountsList) {
      this.coins = 0;
      this.accountsList.forEach(acc => {
        this.coins += acc.coins;
      });
    } else {
      this.globals.onNewAccount.forEach(a => {
        this.coins = 0;
        a.forEach(acc => {
          this.coins += acc.coins;
        });
      });
    }
  }

  /**
   * Get the coins for the current logged in account.
   *
   * @returns {number} coins
   */
  currentAccountCoins(): number {
    // return 0; // NOTE:********************************************************
    return this.globals.currentAccount.coins;
  }
}
