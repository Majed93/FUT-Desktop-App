import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {GlobalService} from '../../../globals/global.service';
import {Player} from '../../../domain/player';
import {ProfitMargin} from '../../../domain/profitMargin';
import {catchError} from 'rxjs/operators';

@Injectable()
export class PlayerListService {

  private fileListPath = this.globals.PLAYERLIST_PATH + '/lists';
  private playerListPath = this.globals.PLAYERLIST_PATH + '/list';
  private addPlayerPath = this.globals.PLAYERLIST_PATH + '/add/';
  private updatePlayerPath = this.globals.PLAYERLIST_PATH + '/update/';
  private resetPriceSetPath = this.globals.PLAYERLIST_PATH + '/resetPriceSet';

  constructor(private http: HttpClient, private globals: GlobalService) {
  }

  getFileName(): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.get(this.globals.endpoint + this.fileListPath, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.handleError));
  }

  getPlayerList(selectedFile): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.post(this.globals.endpoint + this.playerListPath, selectedFile, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.handleError));
  }

  addPlayer(selectedFile: string, player: Player): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.post(this.globals.endpoint + this.addPlayerPath + selectedFile, player, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.handleError));
  }

  updatePlayer(selectedFile: string, player: Player): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.post(this.globals.endpoint + this.updatePlayerPath + selectedFile, player, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.handleError));
  }

  deletePlayer(selectedFile: string, id: number): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.delete(this.globals.endpoint + this.playerListPath + '/' + id + '/' + selectedFile, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.handleError));
  }

  resetPriceSet(selectedFile: string): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.post(this.globals.endpoint + this.resetPriceSetPath, selectedFile, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.handleError));
  }

  updatePrices(selectedFile: string, playerList: Player[],
               profitMarginsFilters: ProfitMargin[], minBid: number): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.post(this.globals.endpoint + this.globals.UPDATEPRICES_PATH, {
      list: selectedFile,
      players: playerList,
      profitMargins: profitMarginsFilters,
      minBid: minBid
    }, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.handleError));
  }

  private handleError(error: Response) {
    console.error('Error in an request' + error);
    return throwError(error || 'Server error');
  }
}
