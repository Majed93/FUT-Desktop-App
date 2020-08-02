import {Injectable} from '@angular/core';
import {AbstractWebSocketService} from '../../services/websocket.abstract.service';
import {GlobalService} from '../../globals/global.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {WebSocketService} from '../../services/websocket.service';

@Injectable()
export class UnassignedService extends AbstractWebSocketService {

  /**
   * Constructor.
   *
   * @param http The http client.
   * @param global The {@link GlobalService}
   * @param websocket The {@link WebSocketService
   */
  constructor(private http: HttpClient, private global: GlobalService,
              private websocket: WebSocketService) {
    super(global, websocket);
  }

  /**
   * Get trade pile
   *
   * @param id Id of account.
   * @returns {Observable<>} Auction response of trade pile.
   */
  getUnassignedPile(id): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this.http.post(this.global.endpoint + this.global.UNASSIGNED_PILE_PATH, id, {
      headers: headers, withCredentials: true
    }).pipe(catchError(this.global.handleError));
  }
}
