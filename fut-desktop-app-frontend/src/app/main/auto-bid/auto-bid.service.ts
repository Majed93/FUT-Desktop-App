import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {GlobalService} from '../../globals/global.service';
import {Observable, Subject, Subscription} from 'rxjs';
import {catchError, takeUntil} from 'rxjs/operators';
import {WebSocketService} from '../../services/websocket.service';
import {Message} from '@stomp/stompjs';
import {StatusMessage} from '../../domain/status.message.type';
import {BidCriteriaType} from './bid-criteria/bid.criteria.type';

/**
 * Service to communicate with backend regarding auto bidding.
 */
@Injectable()
export class AutoBidService {

  /** Connected state of websockets */
  private isConnected: boolean;

  /** Modal listeners */
  public modalListenerObservable: Observable<any>;
  private modalSubscription: Subscription;
  private modalListenerSubject: Subject<any>;

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
   * @param {HttpClient} http.
   * @param {GlobalService} globals.
   * @param websocket {WebSocketService} websocket service.
   */
  constructor(private http: HttpClient,
              private globals: GlobalService,
              private websocket: WebSocketService) {
    this.isConnected = false;
  }

  /**
   * Start the bidding process
   *
   * @returns {Observable<>}
   */
  startBid(level, listName, id: number, player: any, bidCriteria: BidCriteriaType): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.post(this.globals.endpoint + this.globals.STARTAUTOBID_PATH,
      {
        id: id,
        list: listName,
        players: player,
        level: level,
        bidCriteria: bidCriteria
      },
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.globals.handleError));
  }

  /**
   * Send option from modal.
   *
   * @param {number} option
   * @param {number} newPriceInput
   * @returns {Observable<>}
   */
  sendOptions(option: number, newPriceInput: number): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.post(this.globals.endpoint + this.globals.AUTOBIDOPTIONS_PATH + newPriceInput,
      option,
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.globals.handleError));
  }

  /**
   * Stop the search.
   */
  stop(): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.get(this.globals.endpoint + this.globals.STOPAUTOSEARCH_PATH,
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.globals.handleError));
  }

  /**
   * Pause the search.
   */
  pause(): Observable<any> {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.globals.csrf_token);
    return this.http.get(this.globals.endpoint + this.globals.PAUSEAUTOSEARCH_PATH,
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.globals.handleError));
  }

  /**
   * Start listening to the websocket.
   */
  startListening(): void {
    if (!this.isConnected) {
      // Start listening
      this.modalListenerSubject = new Subject();
      this.playerARSubject = new Subject();
      this.playerStatusSubject = new Subject();

      this.modalSubscription = this.websocket.startListening(this.globals.WS_TOO_MANY_MODAL)
        .subscribe((message: Message) => {
          const msg: StatusMessage = StatusMessage.transform(message.body);
          this.modalListenerSubject.next(msg.message);
        });

      this.modalListenerObservable = this.modalListenerSubject.asObservable();

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
   * Disconnect and unsubscribe from websocket.
   */
  stopListening(): void {
    if (this.modalListenerSubject) {
      this.modalListenerSubject.next();
      this.modalListenerSubject.complete();
    }

    if (this.modalListenerObservable) {
      this.modalListenerObservable.subscribe().unsubscribe();
    }

    if (this.modalSubscription) {
      this.modalSubscription.unsubscribe();
    }

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
   * Clears/Resets the messages
   */
  clearMsgs(): void {
    this.playerStatusMsgs = [];
    this.playerARMsgs = null;
  }
}
