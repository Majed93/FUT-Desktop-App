import {Injectable} from '@angular/core';
import {Observable, throwError} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {GlobalService} from '../../globals/global.service';
import {Account} from '../../domain/account';
import {catchError} from 'rxjs/operators';

@Injectable()
export class AccountsService {

  private accounts_endpoint = this.global.endpoint + this.global.ACCOUNT_PATH;

  /**
   * Constructor.
   * @param {HttpClient} _http
   * @param {GlobalService} global
   */
  constructor(private _http: HttpClient, private global: GlobalService) {
  }

  /**
   * Get all accounts
   *
   * @returns {Observable<Account[]>}
   */
  getAccounts(): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this._http.get(this.accounts_endpoint, {
      headers: headers, withCredentials: true
    });
  }

  /**
   * Get one account
   *
   * @returns {Observable<Account[]>}
   */
  getOneAccount(id: number): Observable<Account> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this._http.get<Account>(this.accounts_endpoint + '/' + id, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.handleError));
  }

  addAccount(data): Observable<Account> {
    const headers: HttpHeaders = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this._http.post<Account>(this.accounts_endpoint, data, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.handleError));
  }

  updateAccount(data): Observable<Account> {
    const headers: HttpHeaders = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this._http.put<Account>(this.accounts_endpoint, data, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.handleError));
  }

  deleteAccount(id): Observable<boolean> {
    const headers: HttpHeaders = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this._http.delete<boolean>(this.accounts_endpoint + '/' + id, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.handleError));
  }

  /**
   * Relist all given accounts
   *
   * @param {Account[]} accounts Accounts to relist
   * @param {boolean} isOnlyOne If true, then only one account will be done, otherwise all will be done.
   * @param {number} duration How long to list items for
   * @param {boolean} relistOption If true then relist otherwise list individually
   * @param {boolean} newPrice If list using new price from player lists
   * @param {boolean} watchList If to check watch list or not.
   * @returns {Observable<any>}
   */
  relistAll(accounts: Account[], isOnlyOne: boolean, duration: number,
            relistOption: boolean, newPrice: boolean, watchList: boolean): Observable<any> {
    const headers: HttpHeaders = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this._http.post(this.global.endpoint + this.global.RELIST_ALL_PATH + '/' + relistOption,
      {
        accounts: accounts,
        isOnlyOne: isOnlyOne,
        relistRequest: {
          id: null,
          auctionDuration: duration,
          auctionResponse: null,
          newPrice: newPrice,
          pile: watchList ? 'watchList' : 'tradePile'
        }
      },
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.handleError));
  }

  /**
   * Handle any errors.
   * @param {Response} error
   * @returns {ErrorObservable}
   */
  private handleError(error: Response) {
    console.error('Error in an request' + error);
    return throwError(error || 'Server error');
  }
}
