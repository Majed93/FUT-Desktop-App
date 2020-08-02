import {Component, ElementRef, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {AccountsService} from './accounts.service';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {GlobalService} from '../../globals/global.service';
import {EmailValidator, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Account} from '../../domain/account';
import {AlertService} from '../../alert';
import {Platform} from '../../enums/platform';
import {IMultiSelectOption, IMultiSelectSettings} from 'angular-2-dropdown-multiselect';
import {Subject} from 'rxjs';
import {WebSocketService} from '../../services/websocket.service';
import {takeUntil} from 'rxjs/operators';
import {Message} from '@stomp/stompjs';
import {StatusMessage} from '../../domain/status.message.type';

declare const jquery: any;
declare const $: any;

@Component({
  moduleId: module.id,
  selector: 'app-accounts',
  templateUrl: 'accounts.component.html',
  styleUrls: ['accounts.component.scss']
})
export class AccountsComponent implements OnInit, OnDestroy {

  accounts: Account[];
  /** Array of account ids */
  @Input() listingAccounts = [];
  listingAccountsOptions: IMultiSelectOption[];
  error: string;
  authSuccess: boolean;
  // numberOfAccountsLimit: boolean; // This should indicate whether a user
  // has reach the limit of the number of accounts they can have
  accountAddForm: FormGroup;
  accountUpdateForm: FormGroup;
  accountsMsg: string;
  accountAddFormMsg: string;
  accountUpdateFormMsg: string;
  page = 1;
  maxSize = 10;
  coins = 0;
  @Input() listDuration: number;
  @Input() relistOption = true;
  @Input() newPrice = false;
  @Input() watchList = false;
  @Input() isOnlyOneAccount = false;
  platforms;
  // Need to get a list of modal elements
  @ViewChild('viewUpdateFormModalClose') viewUpdateFormModalClose: ElementRef;
  @ViewChild('deleteAccountModalClose') deleteAccountModalClose: ElementRef;
  @ViewChild('addFormModalClose') addFormModalClose: ElementRef;
  @ViewChild('TwofaFormModalClose') TwofaFormModalClose: ElementRef;

  disableCloseModal: boolean;

  /** Connected state of websockets */
  isConnected: boolean;

  pauseEndpoint = this.globals.endpoint + this.globals.PAUSERELISTING_PATH;
  stopEndpoint = this.globals.endpoint + this.globals.STOPRELISTING_PATH;

  /** Status received from the status queue on player search */
  statusSubject: Subject<any>;
  status = [];
  textareaText: string;

  // Settings configuration
  selectSettings: IMultiSelectSettings = {
    checkedStyle: 'glyphicon',
    buttonClasses: 'btn btn-dark',
    itemClasses: 'btn btn-dark',
    dynamicTitleMaxItems: 3,
    displayAllSelectedText: true,
    showCheckAll: true,
    showUncheckAll: true
  };

  /**
   * Constructor.
   *
   * @param {AccountsService} accountsService
   * @param {GlobalService} globals
   * @param {ActivatedRoute} activatedRoute
   * @param {AlertService} alertService
   * @param {Router} router
   * @param {FormBuilder} fb
   * @param websocket {@link WebSocketService}
   */
  constructor(private accountsService: AccountsService, private globals: GlobalService,
              private activatedRoute: ActivatedRoute,
              private alertService: AlertService,
              private router: Router, private fb: FormBuilder,
              private websocket: WebSocketService) {
    this.platforms = this.globals.platforms;
    this.createForm();
    this.isConnected = false;
  }

  /**
   * Create the form.
   */
  createForm(): void {
    // Forms Controls for add form
    this.accountAddForm = this.fb.group({
      email: ['', EmailValidator],
      password: ['', Validators.required],
      answer: ['', Validators.required],
      secretKey: ['', Validators.required],
      platform: ['', Validators.required]
    });

    // Forms Controls for update form
    this.accountUpdateForm = this.fb.group({
      id: [''],
      email: ['', EmailValidator],
      password: ['', Validators.required],
      answer: ['', Validators.required],
      secretKey: ['', Validators.required],
      platform: ['', Validators.required]
    });
    // this.accountUpdateForm.controls['platform'].setValue(this , {onlySelf: true})
  }

  /**
   * On initialisation
   */
  ngOnInit(): void {
    // TODO: Need to ensure this doesn't happen each time we go to this section. Currently the logged in toast appears each time.
    this.activatedRoute.params.subscribe((params: Params) => {
      this.authSuccess = params['auth'];

      if (this.authSuccess) {
        this.alertService.show('Successfully logged in.', {cssClass: 'success'});
      }
    });
    this.getAccounts();
    this.startWs();
    this.disableCloseModal = true;
  }

  /**
   * Start listening to the websocket channel
   */
  startWs() {
    if (!this.isConnected) {
      this.statusSubject = new Subject();
      // Start listening to the websocket
      this.websocket.startListening(this.globals.WS_PLAYER_LISTING)
        .pipe(takeUntil(this.statusSubject)).subscribe((message: Message) => {
        const msg: StatusMessage = StatusMessage.transform(message.body);
        this.status.push(msg.message);
        this.textareaText = this.status.join('\n');
      });
      this.isConnected = true;
    }
  }

  /**
   * On component being destroyed.
   */
  ngOnDestroy(): void {
    if (this.statusSubject) {
      this.statusSubject.next();
      this.statusSubject.complete();
    }
    this.status = [];
    this.textareaText = '';
    this.isConnected = false;
  }

  /**
   * Get all accounts.
   */
  getAccounts(): void {
    this.accountsService.getAccounts().subscribe(resp => {
      this.accounts = resp;
      this.listingAccountsOptions = [];
      this.listingAccounts = [];
      this.accounts.forEach(acc => {
        this.listingAccountsOptions.push({id: acc.id, name: acc.email});
        this.listingAccounts.push(acc.id);
      });

      this.globals.newAccount(this.accounts);
    }, error => {
      this.error = 'Error getting all accounts: ' + error.error.message;
      console.error('Error getting all accounts:', error);
    });
  }

  /**
   * Add account.
   */
  onAddForm() {
    const formModel = this.accountAddForm.value;
    this.accountsService.addAccount(formModel).subscribe(() => {
      this.accountsMsg = 'Account added successfully.';
      this.createForm();
      this.getAccounts();
      this.closeModalsAndResetMsgs();
      this.alertService.show(this.accountsMsg, {cssClass: 'success'});
    }, err => {
      this.accountAddFormMsg = 'Error adding: ' + err.error.message;
      console.error('Error adding an account: ', err);
    });
  }

  /**
   * Update account.
   */
  onUpdateForm() {
    const formModel = this.accountUpdateForm.value;
    this.accountsService.updateAccount(formModel).subscribe(() => {
      this.accountsMsg = 'Account updated successfully.';
      this.createForm();
      this.getAccounts();
      this.closeModalsAndResetMsgs();
      this.alertService.show(this.accountsMsg, {cssClass: 'success'});
    }, err => {
      this.accountUpdateFormMsg = 'Error updating: ' + err.error.message;
      console.log('Error adding an account: ' + JSON.stringify(err));
    });
  }

  /**
   * Delete account.
   */
  onDelete() {
    const formModel = this.accountUpdateForm.value;
    const id = formModel.id;
    this.accountsService.deleteAccount(id).subscribe(() => {
      this.accountsMsg = 'Account deleted successfully.';
      this.createForm();
      this.getAccounts();
      this.closeModalsAndResetMsgs();
      this.alertService.show(this.accountsMsg, {cssClass: 'success'});
    }, err => {
      this.accountUpdateFormMsg = 'Error updating: ' + err.error.message;
      console.error('Error adding an account: ' + JSON.stringify(err));
    });
  }

  /**
   * Click on account row.
   *
   * @param account
   */
  rowClick(account) {
    // Forms Controls
    this.accountUpdateForm = this.fb.group({
      id: [account.id],
      email: [account.email, EmailValidator],
      password: [account.password, Validators.required],
      answer: [account.answer, Validators.required],
      secretKey: [account.secretKey, Validators.required],
      platform: [Platform[Platform[account.platform]], Validators.required]
    });
  }

  /**
   * Get platform in user readable format.
   *
   * @param {string} platformKey
   * @returns {string}
   */
  getPlatform(platformKey: string): string {
    return this.globals.getPlatform(platformKey);
  }

  /**
   * Close all modals and clear messages.
   */
  closeModalsAndResetMsgs() {
    this.viewUpdateFormModalClose.nativeElement.click();
    this.deleteAccountModalClose.nativeElement.click();
    this.addFormModalClose.nativeElement.click();
    this.TwofaFormModalClose.nativeElement.click();
    setTimeout(() => {
      this.accountAddFormMsg = null;
      this.accountUpdateFormMsg = null;
      this.accountsMsg = null;
      this.error = null;
    }, 4000); // This time corresponds with the alerting.
  }

  /**
   * Starting re listing all accounts.
   */
  reListAllAccounts() {
    this.disableCloseModal = true;
    this.textareaText = '';
    this.status = [];
    const accountsToRelist: Account[] = [];

    // Doing it this way so it can preserve order.
    this.listingAccountsOptions.forEach(lAO => {
      if (this.listingAccounts.find(a => a === lAO.id)) {
        accountsToRelist.push(this.accounts.find(a => a.id === lAO.id));
      }
    });

    // Start re-list process
    this.accountsService.relistAll(accountsToRelist,
      this.isOnlyOneAccount,
      this.listDuration,
      this.relistOption,
      this.newPrice, this.watchList).subscribe(() => {
      // hide the modal.
      this.alertService.show('Relisted all accounts', {cssClass: 'success'});
      this.disableCloseModal = false;
      this.getAccounts();
    }, error => {
      console.error('Error in relisting all: ', error);
      this.disableCloseModal = false;
      this.alertService.show('Error listing: ' + JSON.stringify(error), {cssClass: 'danger'});
    });
  }

  /**
   * State of relist all accounts button.
   *
   * @returns {boolean}
   */
  relistBtnDisabled() {
    return this.relistOption === false && !this.listDuration;
  }

  /**
   * Shuffle accounts to list
   */
  shuffleAccounts(): void {
    const newArray = this.shuffle(this.listingAccountsOptions);
    this.listingAccountsOptions = [];
    this.listingAccounts = [];
    newArray.forEach(na => {
      this.listingAccountsOptions.push({id: na.id, name: na.name});
      this.listingAccounts.push(na.id);
    });
  }

  shuffle(arr) {
    let i, j, temp;
    for (i = arr.length - 1; i > 0; i--) {
      j = Math.floor(Math.random() * (i + 1));
      temp = arr[i];
      arr[i] = arr[j];
      arr[j] = temp;
    }
    return arr;
  }
}
