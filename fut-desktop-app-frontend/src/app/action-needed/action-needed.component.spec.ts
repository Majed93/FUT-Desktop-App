import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {CUSTOM_ELEMENTS_SCHEMA, DebugElement} from '@angular/core';
import {ActionCodes, ActionNeededComponent} from './action-needed.component';
import {ActionNeededService} from './action-needed.service';
import {HttpClientModule} from '@angular/common/http';
import {BrowserModule, By} from '@angular/platform-browser';
import {Message} from '@stomp/stompjs';

/**
 * Test {@link ActionNeededComponent}
 */
describe('ActionNeededComponent', () => {
  let component: ActionNeededComponent;
  let fixture: ComponentFixture<ActionNeededComponent>;
  let service: ActionNeededService;

  /** The number of messages in the list. */
  const expectMsgListSize = 3;

  // Expected messages

  /**
   * Captcha needed message
   */
  const CAPTCHA_NEEDED_MSG = 'Captcha Verification is required. Please login to the webapp to manually resolve this.';

  /**
   * Soft ban message
   */
  const SOFT_BAN_MSG = 'Oops, looks like you\'ve made too many requests. ' +
    'You\'ve been soft banned. Please try again in about 10/15 minutes.';

  /**
   * Token expired message
   */
  const TOKEN_EXPIRED_MSG = 'Token expired. Please login to your account again.';

  /**
   * The default message to show.
   */
  const DEFAULT_MSG = 'Looks like something went wrong with your FUT account, please check the web app to ensure everything is ok.';

  /**
   * Returns the open modal.
   */
  const getModal = (): DebugElement => {
    return fixture.debugElement.query(By.css('.show'));
  };

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
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ActionNeededComponent
      ],
      imports: [
        BrowserModule,
        HttpClientModule
      ],
      providers: [
        ActionNeededService
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents();
  }));

  /**
   * Run before each test.
   */
  beforeEach(() => {
    service = TestBed.get(ActionNeededService);
    fixture = TestBed.createComponent(ActionNeededComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /**
   * Verify component created.
   */
  it('should create the component', () => {
    const spyService = spyOn(service, 'startListening');
    component.ngOnInit();
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(component.msg).toBeFalsy();
    expect(component.messageList.size).toBe(expectMsgListSize);
    expect(spyService).toHaveBeenCalled();
    expect(getModal()).toBeNull();
  });

  /**
   * Verify correct message is set with action code.
   */
  it('should build the correct message according the action code', () => {
    component.buildMsg(ActionCodes.CAPTCHA_NEEDED);
    expect(component.msg).toBe(CAPTCHA_NEEDED_MSG);

    component.buildMsg(ActionCodes.TOO_MANY_REQUESTS);
    expect(component.msg).toBe(SOFT_BAN_MSG);

    component.buildMsg(ActionCodes.TOKEN_EXPIRED);
    expect(component.msg).toBe(TOKEN_EXPIRED_MSG);

    component.buildMsg(123);
    expect(component.msg).toBe(DEFAULT_MSG);
  });
});
