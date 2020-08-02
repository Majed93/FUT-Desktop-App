import {Subject} from 'rxjs';
import {GlobalService} from '../globals/global.service';
import {WebSocketService} from './websocket.service';
import {takeUntil} from 'rxjs/operators';
import {Message} from '@stomp/stompjs';
import {StatusMessage} from '../domain/status.message.type';

/**
 * Abstract web socket service extended by other services.
 * Contains general channel for listening to listing status.
 */
export abstract class AbstractWebSocketService {

  /** Connected state of websockets */
  private isConnected: boolean;

  /** List status subscription.*/
  private listStatusSubject: Subject<any>;

  /** List status messages. */
  private listStatusMsgs: Array<string> = [];

  // Get the list of status messages.
  get getListenStatusMsg(): Array<string> {
    return this.listStatusMsgs;
  }

  /**
   * Constructor
   *
   * @param {GlobalService} baseGlobal
   * @param {WebSocketService} baseWebsocketService
   */
  protected constructor(private baseGlobal: GlobalService, private baseWebsocketService: WebSocketService) {
    this.isConnected = false;
  }

  /**
   * Start listening to the websocket channel
   */
  startListening(): void {
    if (!this.isConnected) {
      this.stopListening();
      this.listStatusSubject = new Subject();
      this.baseWebsocketService.startListening(this.baseGlobal.WS_PLAYER_LISTING)
        .pipe(takeUntil(this.listStatusSubject)).subscribe((message: Message) => {
        const msg: StatusMessage = StatusMessage.transform(message.body);
        this.listStatusMsgs.push(msg.message);
      });
      this.isConnected = true;
    }
  }

  /**
   * Stop listening and clean up from web socket.
   */
  stopListening(): void {
    if (this.listStatusSubject) {
      this.listStatusSubject.next();
      this.listStatusSubject.complete();
    }
    this.clearMessages();
    this.isConnected = false;
  }

  /**
   * Clear the messages.
   */
  clearMessages(): void {
    this.listStatusMsgs = [];
  }
}
