import {ActionNeededService} from './action-needed.service';
import {async, TestBed} from '@angular/core/testing';
import {WebSocketService} from '../services/websocket.service';
import {HttpClientModule} from '@angular/common/http';
import {GlobalService} from '../globals/global.service';
import {Message} from '@stomp/stompjs';
import {of} from 'rxjs';

/**
 * Tests {@link ActionNeededService}
 */
describe('ActionNeededService', () => {
  let actionNeededService: ActionNeededService;
  let webSocketSpy: WebSocketService;
  let global: GlobalService;

  /**
   * Runs before each test.
   */
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule
      ],
      providers: [
        WebSocketService,
        ActionNeededService
      ]
    }).compileComponents();
  }));

  const mockResponse: Message = {
    body: '458',
    ack: headers => {
    },
    binaryBody: null,
    command: '',
    headers: null,
    isBinaryBody: false,
    nack: headers => {
    }
  };

  /**
   * Run before each test.
   */
  beforeEach(() => {
    actionNeededService = TestBed.get(ActionNeededService);
    webSocketSpy = TestBed.get(WebSocketService);
    global = TestBed.get(GlobalService);
  });

  /**
   * Verify service created.
   */
  it('should init the service', () => {
    expect(actionNeededService).toBeTruthy();
  });

  /**
   * Verify service starts listening to the socket.
   */
  it('should start listening to the socket', () => {
    const spyOnListening = spyOn(webSocketSpy, 'startListening').and.returnValue(of(mockResponse));
    actionNeededService.startListening();
    expect(spyOnListening).toHaveBeenCalledWith(global.WS_ACTION_NEEDED);
    expect(actionNeededService.actionListenerObservable).toBeTruthy();
  });

  /**
   * Can't actually verify anything here.
   */
  it('should stop listening to the socket', () => {
    actionNeededService.startListening();

    actionNeededService.stopListening();
  });
});
