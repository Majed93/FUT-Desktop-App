import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {GlobalService} from '../../../globals/global.service';
import {catchError, takeUntil} from 'rxjs/operators';
import {WebSocketService} from '../../../services/websocket.service';
import {Message} from '@stomp/stompjs';
import {StatusMessage} from '../../../domain/status.message.type';

/**
 * Player prices service.
 */
@Injectable()
export class PlayerPricesService {

  /** Connected state of websockets */
  private isConnected: boolean;

  /** Player search status subscription. */
  private playerStatusSubject: Subject<any>;

  /** Array of messages for the player status channel. */
  private playerStatusMsgs: Array<string> = [];

  /** Player auction response subscription. */
  private playerARSubject: Subject<any>;

  /** The message from the player auction response channel. */
  private playerARMsgs: any;

  // Getters

  // Get the player status subject
  get playerSearchStatusMsg(): Array<string> {
    return this.playerStatusMsgs;
  }

  // Get the player auction response subject
  get playerAuctionResp(): any {
    return this.playerARMsgs;
  }

  /**
   * Constructor.
   *
   * @param http Http Client.
   * @param globals Global Service.
   * @param websocket WebSocket Service.
   */
  constructor(private http: HttpClient,
              private globals: GlobalService,
              private websocket: WebSocketService) {
    this.isConnected = false;
  }

  /**
   * Start listening to the websocket.
   */
  startListening(): void {
    if (!this.isConnected) {
      this.clearMsgs();
      this.playerARSubject = new Subject();
      this.playerStatusSubject = new Subject();
      // Start listening
      this.websocket.startListening(this.globals.WS_PLAYER_SEARCH_START)
        .pipe(takeUntil(this.playerARSubject))
        .subscribe((message: Message) => {
          this.playerARMsgs = JSON.parse(message.body);
        });

      this.websocket.startListening(this.globals.WS_PLAYER_SEARCH_STATUS)
        .pipe(takeUntil(this.playerStatusSubject))
        .subscribe((message: Message) => {
          const msg: StatusMessage = StatusMessage.transform(message.body);
          this.playerStatusMsgs.push(msg.message);
        });
      this.isConnected = true;
    }
  }

  /**
   * Start search
   *
   * @param players List of players
   * @param level Level
   * @param listName Name of lists being used. Array.
   * @param id Id of account
   * @returns {Observable<>} Results
   */
  startSearch(players: any, level, listName, id: number): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.post(this.globals.endpoint + this.globals.STARTAUTOSEARCH_PATH,
      {
        id: id,
        list: listName,
        players: players,
        level: level
      },
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.globals.handleError));
  }

  /**
   * Stop listening to web services.
   */
  stopListening() {
    if (this.playerARSubject) {
      this.playerARSubject.next();
      this.playerARSubject.complete();
    }

    if (this.playerStatusSubject) {
      this.playerStatusSubject.next();
      this.playerStatusSubject.complete();
    }
    this.clearMsgs();
    this.isConnected = false;
  }

  /**
   * Stop the search.
   */
  stop() {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.get(this.globals.endpoint + this.globals.STOPAUTOSEARCH_PATH,
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.globals.handleError));
  }

  /**
   * Pause the search.
   */
  pause() {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.get(this.globals.endpoint + this.globals.PAUSEAUTOSEARCH_PATH,
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.globals.handleError));
  }

  /**
   * Clears/Resets the messages
   */
  clearMsgs(): void {
    this.playerStatusMsgs = [];
    this.playerARMsgs = null;
  }
}
