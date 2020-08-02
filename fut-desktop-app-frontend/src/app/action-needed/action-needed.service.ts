import {Injectable} from '@angular/core';
import {GlobalService} from '../globals/global.service';
import {WebSocketService} from '../services/websocket.service';
import {Observable, Subject, Subscription} from 'rxjs';
import {Message} from '@stomp/stompjs';

/**
 * Service to handle calls for when urgent actions are needed by the user
 */
@Injectable()
export class ActionNeededService {

  /** Connected state of websockets */
  private isConnected: boolean;

  /** Action needed listeners */
  public actionListenerObservable: Observable<any>;
  private actionSubscription: Subscription;
  private actionSubject: Subject<any>;

  /**
   * Constructor.
   *
   * @param global The {@link GlobalService}
   * @param websocket The {@link WebSocketService}
   */
  constructor(private global: GlobalService, private websocket: WebSocketService) {
    this.isConnected = false;
  }

  /**
   * Start listening to the websocket.
   */
  startListening(): void {
    if (!this.isConnected) {
      // Start listening
      this.actionSubject = new Subject();

      this.actionSubscription = this.websocket.startListening(this.global.WS_ACTION_NEEDED)
        .subscribe((message: Message) => {
          this.actionSubject.next(message.body);
        });

      this.actionListenerObservable = this.actionSubject.asObservable();
    }
  }

  /**
   * Disconnect and unsubscribe from the websocket.
   */
  stopListening(): void {
    if (this.actionSubject) {
      this.actionSubject.next();
      this.actionSubject.complete();
    }

    this.actionListenerObservable.subscribe().unsubscribe();
    this.actionSubscription.unsubscribe();
    this.isConnected = false;
  }
}
