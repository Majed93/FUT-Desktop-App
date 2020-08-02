import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {GlobalService} from '../../globals/global.service';
import {Observable, throwError} from 'rxjs';
import {MarketBody} from '../../domain/marketBody';
import {catchError} from 'rxjs/operators';

@Injectable()
export class SearchService {

  constructor(private http: HttpClient, private globals: GlobalService) {
    // NOOP
  }

  /**
   * Search for given item
   *
   * @param {MarketBody} marketBody
   * @param firstSearch firstSearch
   * @returns {Observable<>}
   */
  search(marketBody: MarketBody, firstSearch: boolean): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.post(this.globals.endpoint + this.globals.SEARCHMARKET_PATH + '/' + firstSearch + '/' + this.globals.currentAccount.id,
      marketBody,
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.handleError));
  }

  /**
   * Bid item
   *
   * @param {number} id Account id.
   * @param info Auction info.
   * @param {number} amount Bid amount.
   * @returns {Observable<>}
   */
  bid(id: number, info: any, amount: number): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.post(this.globals.endpoint + this.globals.BIDITEM_PATH,
      {
        id: id,
        auctionInfo: info,
        amount: amount,
        bin: false
      },
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.handleError));
  }

  /**
   * BIN given item
   *
   * @param {number} id Account id.
   * @param info Auction info.
   * @returns {Observable<>}
   */
  bin(id: number, info: any): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.post(this.globals.endpoint + this.globals.BINITEM_PATH,
      {
        id: id,
        auctionInfo: info,
        amount: 0, // BINning so don't really care about this value.
        bin: true
      },
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.handleError));
  }

  /**
   * Handle errors
   *
   * @param {Response} error Error to handle
   * @returns {ErrorObservable} Error message.
   */
  private handleError(error: Response) {
    console.error('Error in an request' + error);
    return throwError(error || 'Server error');
  }
}
