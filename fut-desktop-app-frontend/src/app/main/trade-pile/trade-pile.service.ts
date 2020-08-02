import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {GlobalService} from '../../globals/global.service';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {AbstractWebSocketService} from '../../services/websocket.abstract.service';
import {WebSocketService} from '../../services/websocket.service';

/**
 * Trade pile service.
 */
@Injectable()
export class TradePileService extends AbstractWebSocketService implements OnDestroy {
  private tradePilePath = this.global.TRADEPILE_PATH;
  private removeSoldPath = this.global.REMOVESOLD_PATH;
  private reListPath = this.global.RELIST_PATH;
  private listPath = this.global.LISTITEM_PATH;
  private removeSoldItemPath = this.global.REMOVESOLDITEM_PATH;
  private autoListUnlistedPath = this.global.AUTOLISTUNLISTED_PATH;

  private firstListing = false;
  private timer: any;

  /**
   * Constructor.
   * @param _http The http client
   * @param global {@link GlobalService}
   * @param websocket {@link WebSocketService}
   */
  constructor(private _http: HttpClient,
              private global: GlobalService,
              private websocket: WebSocketService) {
    super(global, websocket);
  }

  /**
   * Get trade pile
   *
   * @param id Id of account.
   * @returns {Observable<>} Auction response of trade pile.
   */
  getTradePile(id): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this._http.post(this.global.endpoint + this.tradePilePath, id, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.global.handleError));
  }

  /**
   * Remove sold.
   * @param id Id of account
   * @returns {Observable<>} Auction response of updated trade pile.
   */
  removeSold(id): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this._http.post(this.global.endpoint + this.removeSoldPath, id, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.global.handleError));
  }

  /**
   * Re list all.
   *
   * @param {number} id Id of account.
   * @param {boolean} reListAll true = re list all for same time, false = re list all for given time.
   * @param duration used to list them for.
   * @param auctionResponse Auction response.
   * @param newPrice true = use new price, false = keep same,
   */
  reList(id: number, reListAll: boolean, duration: number, auctionResponse: any, newPrice: boolean) {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this._http.post(this.global.endpoint + this.reListPath + '/' + reListAll, {
        id: id,
        auctionDuration: duration,
        auctionResponse: auctionResponse,
        newPrice: newPrice,
        watchList: 'tradePile'
      },
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.global.handleError));
  }

  /**
   * List item.
   *
   * @returns {Observable<>}
   */
  listItem(id: number, auctionDetails: any, listType: string, pile: any): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    if (this.firstListing) {
      this.startFirstOptionTimeout();
    } else {
      this.firstListing = true;
    }
    return this._http.post(this.global.endpoint + this.listPath + '/' + this.firstListing + '/' + listType, {
        id: id,
        auctionDetails: auctionDetails,
        auctionResponse: pile
      },
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.global.handleError));
  }

  /**
   * Auto list unlisted items
   *
   * @param {number} id Id of account
   * @param duration Auction duration
   * @param tradePile Trade pile
   * @param {boolean} newPrice
   * @returns {Observable<>}
   */
  autoListUnlisted(id: number, duration: any, tradePile: any, newPrice: boolean): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this._http.post(this.global.endpoint + this.autoListUnlistedPath, {
        id: id,
        auctionDuration: duration,
        auctionResponse: tradePile,
        newPrice: newPrice,
        watchList: 'tradePile'
      },
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.global.handleError));
  }

  startFirstOptionTimeout() {
    clearInterval(this.timer);
    this.timer = setInterval(() => {
      this.firstListing = false;
      clearInterval(this.timer);
    }, (1000 * 60 * 60));
  }

  ngOnDestroy(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }

  /**
   * Remove sold item. TODO
   *
   * @returns {Observable<any>}
   */
  removeSoldItem(id: number): Observable<any> {
    return null;
  }

  /**
   * Send item back to club. TODO
   *
   * @returns {Observable<any>}
   */
  sendBackToClub(id: number): Observable<any> {
    return null;
  }

  /**
   * Quick sell item. TODO
   *
   * @returns {Observable<any>}
   */
  quickSell(id: number): Observable<any> {
    return null;
  }
}
