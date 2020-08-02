import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActionNeededService} from './action-needed.service';
import {GlobalService} from '../globals/global.service';
import {Platform} from '../enums/platform';

// jQuery imports for modal.
declare const jquery: any;
declare const $: any;

export const ActionCodes = {
  TOKEN_EXPIRED: 426,
  TOO_MANY_REQUESTS: 429,
  CAPTCHA_NEEDED: 458
};

@Component({
  moduleId: module.id,
  selector: 'fut-action-needed',
  templateUrl: 'action-needed.component.html',
  styleUrls: ['action-needed.component.scss']
})
export class ActionNeededComponent implements OnInit, OnDestroy {

  /** The action code */
  msg: string;

  /** A map of codes along with their messages */
  messageList: Map<number, string>;

  /**
   * Captcha needed message
   */
  private readonly CAPTCHA_NEEDED_MSG = 'Captcha Verification is required. Please login to the webapp to manually resolve this.';

  /**
   * Soft ban message
   */
  private readonly SOFT_BAN_MSG = 'Oops, looks like you\'ve made too many requests. ' +
    'You\'ve been soft banned. Please try again in about 10/15 minutes.';

  /**
   * Token expired message
   */
  private readonly TOKEN_EXPIRED_MSG = 'Token expired. Please login to your account again.';

  /**
   * The default message to show.
   */
  private readonly DEFAULT_MSG = 'Looks like something went wrong with your FUT account, please check the web app to ensure everything is ok.';

  /** The account affected */
  accountAffect: string;

  /**
   * Constructor.
   *
   * @param actionNeededService Handle to {@link ActionNeededService}
   * @param global Handle to {@link GlobalService}
   */
  constructor(private actionNeededService: ActionNeededService,
              private global: GlobalService) {
    // NOOP
  }

  /**
   * On initialization.
   */
  ngOnInit(): void {
    this.createMessageList();
    this.hideModal();
    this.actionNeededService.startListening();

    this.actionNeededService.actionListenerObservable.subscribe((actionNeededCode) => {
      if (actionNeededCode) {
        console.log(`the code > ${actionNeededCode}`);
        if (this.global.currentAccount) {
          this.accountAffect = this.global.currentAccount.email + ' Platform: ' + Platform[this.global.currentAccount.platform];
        }
        this.buildMsg(Number(actionNeededCode));
        this.showModal();
      }
    });
  }

  /**
   * Create the message list.
   */
  createMessageList(): void {
    this.messageList = new Map<number, string>();
    this.messageList.set(ActionCodes.TOKEN_EXPIRED, this.TOKEN_EXPIRED_MSG);
    this.messageList.set(ActionCodes.TOO_MANY_REQUESTS, this.SOFT_BAN_MSG);
    this.messageList.set(ActionCodes.CAPTCHA_NEEDED, this.CAPTCHA_NEEDED_MSG);
  }

  /**
   * Build the message
   *
   * @param actionNeededCode The action code
   */
  buildMsg(actionNeededCode: number): void {
    switch (actionNeededCode) {
      case ActionCodes.TOKEN_EXPIRED:
        this.msg = this.TOKEN_EXPIRED_MSG;
        break;
      case ActionCodes.TOO_MANY_REQUESTS:
        this.msg = this.SOFT_BAN_MSG;
        break;
      case ActionCodes.CAPTCHA_NEEDED:
        this.msg = this.CAPTCHA_NEEDED_MSG;
        break;
      default:
        this.msg = this.DEFAULT_MSG;
        break;
    }
  }

  /**
   * Close the modal.
   */
  closeModal(): void {
    this.hideModal();
  }

  /**
   * On destroy.
   */
  ngOnDestroy(): void {
    this.hideModal();
    this.actionNeededService.stopListening();
  }

  /**
   * Hide the modal.
   */
  hideModal(): void {
    $('#actionNeededModal').modal('hide');
  }

  /**
   * Show the modal.
   */
  showModal(): void {
    $('#actionNeededModal').modal('show');
  }
}
