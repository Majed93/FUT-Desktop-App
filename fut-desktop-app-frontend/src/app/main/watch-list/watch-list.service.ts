import {Injectable} from '@angular/core';
import {GlobalService} from '../../globals/global.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {AbstractWebSocketService} from '../../services/websocket.abstract.service';
import {WebSocketService} from '../../services/websocket.service';

@Injectable()
export class WatchListService extends AbstractWebSocketService {
  private watchListPath = this.global.WATCHLIST_PATH;
  private moveToTradePath = this.global.MOVETOTRADE_PATH;
  private moveToAndListPath = this.global.MOVANDLIST_PATH;
  private removeItemFromWatchListPath = this.global.REMOVEFROMWATCHLIST_PATH;

  /**
   * Constructor.
   * @param http The http client.
   * @param global A handle to {@link GlobalService}
   * @param websocketService {@ WebSocketService}
   */
  constructor(private http: HttpClient, private global: GlobalService,
              private websocketService: WebSocketService) {
    super(global, websocketService);
  }

  /**
   * Get watch list
   *
   * @param id
   * @returns {Observable<>}
   */
  getWatchList(id): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this.http.post(this.global.endpoint + this.watchListPath, id, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.global.handleError));
  }

  /**
   * Move all won items to trade pile.
   *
   * @param id Id of account
   * @param watchList watch list.
   * @param tradeId Item to move. 0 if whole list to process.
   * @param pile watchList or unassigned
   * @returns {Observable<>}
   */
  moveToTrade(id: number, watchList: any, tradeId: number, pile: string): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this.http.post(this.global.endpoint + this.moveToTradePath + '/' + tradeId, {
      id: id,
      auctionResponse: watchList,
      removeExpired: false,
      pile: pile
    }, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.global.handleError));
  }

  /**
   * Move all won items to trade pile AND list them.
   *
   * @param id Id of account
   * @param list watch list.
   * @param duration Item to move. 0 if whole list to process.
   * @param pile watchList or unassigned
   * @returns {Observable<>}
   */
  autoList(id: number, list: any, duration: any, pile: string): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this.http.post(this.global.endpoint + this.moveToAndListPath, {
      id: id,
      auctionDuration: duration,
      auctionResponse: list,
      newPrice: false,
      pile: pile
    }, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.global.handleError));
  }

  /**
   * Remove expired or outbidded ALL items.
   *
   * @param id Id of account.
   * @param watchList Watch list.
   * @param removeExpired true = remove only expired, false = remove expired + outbidded.
   * @returns {Observable<>}
   */
  removeExpiredOutbidded(id, watchList: any, removeExpired: boolean): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this.http.post(this.global.endpoint + this.removeItemFromWatchListPath + '/0', {
      id: id,
      auctionResponse: watchList,
      removeExpired: removeExpired
    }, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.global.handleError));
  }

  /**
   * Remove expired or outbidded item.
   *
   * @param id Id of account.
   * @param tradeId trade id.
   * @returns {Observable<>}
   */
  removeItemFromWatchList(id, tradeId: number): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this.http.post(this.global.endpoint + this.removeItemFromWatchListPath + '/' + tradeId, {
      id: id,
      auctionResponse: null,
      removeExpired: false
    }, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.global.handleError));
  }
}
