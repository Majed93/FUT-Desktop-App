import {Injectable} from '@angular/core';
import {RxStomp} from '@stomp/rx-stomp';
import {GlobalService} from '../globals/global.service';
import {Observable} from 'rxjs';
import {IMessage} from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  /** Holds websocket configured and listeners */
  private rxStomp: RxStomp;

  constructor(private global: GlobalService) {
    this.rxStomp = new RxStomp();
  }

  /**
   * Connect to the websocket
   */
  public connect() {
    this.rxStomp.configure({
      brokerURL: this.global.wsEndpoint,
      heartbeatIncoming: 0, // Disabled
      heartbeatOutgoing: 30000, // Every 30 seconds
      reconnectDelay: 1000 // Try reconnect every second if connect lost
    });

    // Start the connection
    this.rxStomp.activate();
  }

  /**
   * Disconnect from the web socket.
   */
  public disonnect(): void {
    this.rxStomp.deactivate();
  }

  /**
   * Start listening to the endpoint.
   * The called of this method should ideally use the following syntax:
   *
   * startListening(String).pipe(takeUntil(Subject)).subscribe(...);
   * @param endpoint
   */
  public startListening(endpoint: string): Observable<IMessage> {
    return this.rxStomp.watch(endpoint);
  }
}
