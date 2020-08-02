import {EventEmitter, Injectable, Output} from '@angular/core';
import {Account} from '../domain/account';
import {Player} from '../domain/player';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {AlertService} from 'app/alert';

/**
 * Global shared service across the app.
 */
@Injectable({
  providedIn: 'root'
})
export class GlobalService {
  endpoint: string;
  service_started = false;
  service_port = 1000; // Service port. Default is -1 or will be read in from the file.
  global_auth;
  csrf_token: string;
  isPlayerResourcesLoaded = false;
  isPlayersLoaded = false;
  readonly API_PATH = 'http://localhost:';
  readonly ACCOUNT_PATH = '/accounts';
  readonly RELIST_ALL_PATH = this.ACCOUNT_PATH + '/relistAll';
  readonly STOPRELISTING_PATH = this.ACCOUNT_PATH + '/relistAll/stop';
  readonly PAUSERELISTING_PATH = this.ACCOUNT_PATH + '/relistAll/pause';
  readonly AUTH_PATH = '/auth';
  readonly LOGIN_PATH = '/login';
  // Trade pile & Watch list.
  readonly TRANSFER_PATH = '/transfer';
  readonly TRADEPILE_PATH = this.TRANSFER_PATH + '/tradePile';
  readonly WATCHLIST_PATH = this.TRANSFER_PATH + '/watchList';
  readonly UNASSIGNED_PILE_PATH = this.TRANSFER_PATH + '/unassigned';
  readonly REMOVESOLD_PATH = this.TRANSFER_PATH + '/removeSold';
  readonly REMOVESOLDITEM_PATH = this.TRANSFER_PATH + '/removeItem';
  readonly RELIST_PATH = this.TRANSFER_PATH + '/reList';
  readonly LISTITEM_PATH = this.TRANSFER_PATH + '/listItem';
  readonly AUTOLISTUNLISTED_PATH = this.TRANSFER_PATH + '/autoListUnlisted';
  readonly MOVETOTRADE_PATH = this.TRANSFER_PATH + '/moveToTrade';
  readonly MOVANDLIST_PATH = this.TRANSFER_PATH + '/moveAndList';
  readonly REMOVEFROMWATCHLIST_PATH = this.TRANSFER_PATH + '/removeFromWatchList';
  readonly STOPLISTING_PATH = this.TRANSFER_PATH + '/stopListing';
  readonly PAUSELISTING_PATH = this.TRANSFER_PATH + '/pauseListing';
  readonly CACHE_STOP = this.TRANSFER_PATH + '/stopCache';

  readonly PLAYERLIST_PATH = '/players';
  readonly ALLPLAYERS_PATH = '/allPlayers';
  readonly TRANSFERMARKET_PATH = '/transferMarket';
  readonly SEARCHMARKET_PATH = this.TRANSFERMARKET_PATH + '/search';
  readonly BIDITEM_PATH = this.TRANSFERMARKET_PATH + '/bidItem';
  readonly BINITEM_PATH = this.TRANSFERMARKET_PATH + '/binItem';
  readonly STARTAUTOSEARCH_PATH = this.TRANSFERMARKET_PATH + '/autoSearch';
  readonly STOPAUTOSEARCH_PATH = this.TRANSFERMARKET_PATH + '/stopSearch';
  readonly PAUSEAUTOSEARCH_PATH = this.TRANSFERMARKET_PATH + '/pauseSearch';
  readonly UPDATEPRICES_PATH = this.TRANSFERMARKET_PATH + '/updatePrices';
  readonly TEAMS_PATH = '/teams';
  readonly NATIONS_PATH = '/nations';
  readonly LEAGUES_PATH = '/leagues';
  readonly POSITIONS_PATH = '/positions';

  readonly STARTAUTOBID_PATH = this.TRANSFERMARKET_PATH + '/autoBid';
  readonly AUTOBIDOPTIONS_PATH = this.TRANSFERMARKET_PATH + '/priceInput/';

  /** Web socket endpoints */
  wsEndpoint: string; // This will be set after inital login to app, with WS_PATH & WS_PREFIX. - use this to setup up websockets
  readonly WS_PATH = 'ws://localhost:';
  readonly WS_PREFIX = '/futws';
  readonly WS_STATUS_PATH = '/status';
  readonly WS_START_PATH = '/start';
  readonly WS_PLAYER_LISTING = '/playerListing' + this.WS_STATUS_PATH;
  readonly WS_PLAYER_SEARCH = '/playerSearch';
  readonly WS_PLAYER_SEARCH_START = this.WS_PLAYER_SEARCH + this.WS_START_PATH;
  readonly WS_PLAYER_SEARCH_STATUS = this.WS_PLAYER_SEARCH + this.WS_STATUS_PATH;
  readonly WS_TOO_MANY_MODAL = '/autoBid/modal';
  readonly WS_ACTION_NEEDED = '/manual/actionNeeded';

  /**
   * JSON of platforms
   */
  platforms = [
    {key: 'Pc', value: 'PC'},
    {key: 'Ps3', value: 'PS3'},
    {key: 'Ps4', value: 'PS4'},
    {key: 'Xbox360', value: 'Xbox 360'},
    {key: 'XboxOne', value: 'Xbox One'}
  ];

  /**
   * Current account logged in
   */
  currentAccount: Account;
  loggedIn = false; // todo: ***********

  /**
   * List of all accounts
   */
  @Output()
  onNewAccount: EventEmitter<Account[]> = new EventEmitter<Account[]>();

  /* Full list of players from EAs JSON */
  allPlayers: Player[];
  allTeams = [];
  allNations = [];
  allLeagues = [];
  allPositions = [];

  /**
   * Constructor
   *
   * @param {HttpClient} http Http Client service.
   * @param alert The alert service
   */
  constructor(private http: HttpClient) {
    // NOOP
  }

  /**
   * Get one account and update the coin count.
   */
  updateCoinCount(): void {
    if (this.currentAccount) {
      const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.csrf_token);

      const result: Observable<Account> = this.http.get<Account>(this.endpoint + this.ACCOUNT_PATH + '/' + this.currentAccount.id, {
        headers: headers, withCredentials: true
      });

      result.subscribe(newAcc => {
        this.currentAccount = newAcc;
      });
    }
  }

  /**
   * Get all players JSON
   *
   */
  getAllPlayersJson(): Observable<any> {
    if (!this.isPlayersLoaded) {
      const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.csrf_token);
      return this.http.get(this.endpoint + this.PLAYERLIST_PATH + this.ALLPLAYERS_PATH, {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.handleError));
    } else {
      return of(this.allPlayers);
    }
  }

  /**
   * Get teams, leagues, nations and positions.
   */
  async getPlayerResourceData() {
    // Get clubs
    this.getAllTeams().subscribe(teams => {
      this.allTeams = teams;
      this.isPlayerResourcesLoaded = true;
    }, error => {
      console.error('Error getting all teams', error);
    });

    // Get nations
    this.getAllNations().subscribe(nations => {
      this.allNations = nations;
      this.isPlayerResourcesLoaded = true;
    }, error => {
      console.error('Error getting all nations', error);
    });

    // Get leagues
    this.getAllLeagues().subscribe(leagues => {
      this.allLeagues = leagues;
      this.isPlayerResourcesLoaded = true;
    }, error => {
      console.error('Error getting all leagues', error);
    });

    // Get positions
    this.getPositions().subscribe(positions => {
      this.allPositions = positions;
      this.isPlayerResourcesLoaded = true;
    }, error => {
      console.error('Error getting all positions', error);
    });
  }

  /**
   * Get all teams
   */
  getAllTeams(): Observable<any> {
    if (!this.isPlayerResourcesLoaded) {
      const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.csrf_token);
      return this.http.get(this.endpoint + this.PLAYERLIST_PATH + this.TEAMS_PATH, {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.handleError));
    } else {
      return of(this.allTeams);
    }
  }

  /**
   * Get all nations
   */
  getAllNations(): Observable<any> {
    if (!this.isPlayerResourcesLoaded) {
      const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.csrf_token);
      return this.http.get(this.endpoint + this.PLAYERLIST_PATH + this.NATIONS_PATH, {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.handleError));
    } else {
      return of(this.allNations);
    }
  }


  /**
   * Get all leagues
   */
  getAllLeagues(): Observable<any> {
    if (!this.isPlayerResourcesLoaded) {
      const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.csrf_token);
      return this.http.get(this.endpoint + this.PLAYERLIST_PATH + this.LEAGUES_PATH, {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.handleError));
    } else {
      return of(this.allLeagues);
    }
  }


  /**
   * Get all positions
   */
  getPositions(): Observable<any> {
    if (!this.isPlayerResourcesLoaded) {
      const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.csrf_token);
      return this.http.get(this.endpoint + this.PLAYERLIST_PATH + this.POSITIONS_PATH, {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.handleError));
    } else {
      return of(this.allPositions);
    }
  }


  /**
   * Handle errors
   *
   * @param {Response} error
   * @returns {ErrorObservable}
   */
  handleError(error: Response) {
    console.error('Error in a request: ', error);
    return throwError(error || 'Server error');
  }

  /**
   * Get platform in user readable format.
   *
   * @param {string} platformKey
   * @returns {string}
   */
  public getPlatform(platformKey: string): string {
    let platform = '';
    this.platforms.forEach(
      p => {
        if (p.key === platformKey) {
          platform = p.value;
        }
      }
    );
    return platform;
  }

  newAccount(accounts: Account[]) {
    this.onNewAccount.emit(accounts);
  }

  /**
   * Round to the nearest acceptable value.
   *
   * @param {number} amount Amount to round.
   * @returns {number} Rounded amount.
   */
  roundToNearest(amount: number) {
    if (amount === 0) {
      return amount;
    } else if (amount < 150) {
      return 150;
    } else if (amount < 1000) {
      return Math.ceil(amount / 50) * 50;
    } else if (amount < 10000) {
      return Math.ceil(amount / 100) * 100;
    } else if (amount < 50000) {
      return Math.ceil(amount / 250) * 250;
    } else if (amount < 100000) {
      return Math.ceil(amount / 500) * 500;
    }
    return Math.ceil(amount / 1000) * 1000;
  }

  /**
   * Returns step value used for inputs
   *
   * @param {number} amount Amount to inspect
   * @returns {number} new step value
   */
  stepValue(amount: number): number {
    if (amount === 0) {
      return amount;
    } else if (amount < 1000) {
      return 50;
    } else if (amount < 10000) {
      return 100;
    } else if (amount < 50000) {
      return 250;
    } else if (amount < 100000) {
      return 500;
    }
    return 1000;
  }

  /**
   * Get the next bid increment.
   *
   * @param {number} amount
   * @returns {any}
   */
  nextBidAmount(amount: number): number {
    return amount + this.stepValue(amount);
  }

  /**
   * Get pretty print player name
   *
   * @param {Player} player Player
   * @returns {string} Pretty printed player name.
   */
  getPlayerName(player: Player): string {
    if (!player.commonName && player.commonName != null) {
      return player.firstName + ' ' + player.lastName;
    } else {
      return player.commonName + ' (' + player.firstName + ' ' + player.lastName + ')';
    }
  }

  /**
   * Remove accent from string.
   * @param str
   * @returns {string}
   */
  removeAccents(str): string {
    const accents = 'ÀÁÂÃÄÅàáâãäåßÒÓÔÕÕÖØòóôõöøĎďDŽdžÈÉÊËèéêëðÇçČčÐÌÍÎÏìíîïÙÚÛÜùúûüĽĹľĺÑŇňñŔŕŠšŤťŸÝÿýŽž';
    const accentsOut = 'AAAAAAaaaaaasOOOOOOOooooooDdDZdzEEEEeeeeeCcCcDIIIIiiiiUUUUuuuuLLllNNnnRrSsTtYYyyZz';
    str = str.split('');
    str.forEach((letter, index) => {
      const i = accents.indexOf(letter);
      if (i !== -1) {
        str[index] = accentsOut[i];
      }
    });
    return str.join('');
  }
}
