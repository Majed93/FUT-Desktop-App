import {AfterViewChecked, ChangeDetectorRef, Component, ElementRef, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {GlobalService} from '../../globals/global.service';
import {AlertService} from '../../alert';
import {AutoBidService} from './auto-bid.service';
import {PlayerListService} from '../players-mgmt/player-list/player-list.service';
import {IMultiSelectOption, IMultiSelectSettings} from 'angular-2-dropdown-multiselect';
import {Player} from '../../domain/player';
import {BidCriteriaComponent} from './bid-criteria/bid-criteria.component';

// jQuery imports for modal.
declare const jquery: any;
declare const $: any;

@Component({
  moduleId: module.id,
  selector: 'app-auto-bid',
  templateUrl: 'auto-bid.component.html',
  styleUrls: ['auto-bid.component.scss']
})
export class AutoBidComponent implements OnInit, OnDestroy, AfterViewChecked {

  /* Array of files */
  fileName: string[];
  /* Selected JSON file. Array of numbers */
  @Input() selectedFiles = [];
  allPlayersFromSelectedFiles: Player[];
  files: IMultiSelectOption[];
  level = 'Any';

  // Settings configuration
  selectSettings: IMultiSelectSettings = {
    enableSearch: true,
    buttonClasses: 'btn btn-dark',
    itemClasses: 'btn btn-dark',
    dynamicTitleMaxItems: 3,
    displayAllSelectedText: true
  };

  newPriceInput = 0;
  autoBidStarted = false;
  paused = false;
  @Input() selectedPlayer: number;

  @Input() cardsPerRow = 5;

  @ViewChild('statusText') private statusTextarea: ElementRef;
  @ViewChild('statusTextContainer') private statusTextContainer: ElementRef;
  @ViewChild('searchResultsBody') private searchResultsBody: ElementRef;
  @Input() statusTextRows = 26;

  @ViewChild(BidCriteriaComponent) bidCriteriaModal: BidCriteriaComponent;


  constructor(private autoBidService: AutoBidService, private globals: GlobalService,
              private alert: AlertService, private playerListService: PlayerListService,
              private cdRef: ChangeDetectorRef) {
  }

  ngAfterViewChecked(): void {
    this.getHeightForStatusText();
    this.cdRef.detectChanges();
  }

  ngOnInit() {
    this.globals.updateCoinCount();

    this.playerListService.getFileName().subscribe(resp => {
      this.fileName = resp;
      const ops = [];
      for (let i = 0; i < this.fileName.length; i++) {
        ops.push({id: i, name: this.fileName[i]});
      }
      this.files = ops;
    }, error => {
      console.error('Error getting files names: ' + JSON.stringify(error));
    });
  }

  /**
   * On component being destroyed.
   */
  ngOnDestroy(): void {
    if (this.autoBidService) {
      this.cleanupWs();
    }
  }

  /**
   * On change.
   * @param event The event.
   */
  onChange(event: Event) {
    this.allPlayersFromSelectedFiles = [];
    this.selectedFiles.forEach((item) => {
      // Get every player from list.
      this.playerListService.getPlayerList(this.fileName[item]).subscribe(resp => {
        resp.forEach(p => {
          this.allPlayersFromSelectedFiles.push(p);
        });
      }, err => {
        console.error('Error getting list: ' + err.message);
      });
    });
  }

  /**
   * Start the bidding process.
   */
  startBid(): void {
    this.autoBidStarted = true;
    this.clear();

    // connect to ws
    this.autoBidService.startListening();

    const lists = [];
    // collate the list.
    this.selectedFiles.forEach((item) => {
      lists.push(this.fileName[item]);
    });

    // subscribe and listen for modal.
    this.autoBidService.modalListenerObservable.subscribe((modal) => {
      // Confirm it is actually too many items value returned.
      console.log('Received from modal: ' + modal);
      if (modal === 'TOO_MANY_ITEMS') {
        this.makeAuctionResponse().forEach(aI => {
          if (aI.buyNowPrice < this.newPriceInput) {
            this.newPriceInput = aI.buyNowPrice;
          }
        });
        $('#tooManyModal').modal('show');
      }
    });

    // start the bidding.
    const selectedPlayerRequest = [];
    selectedPlayerRequest.push(this.allPlayersFromSelectedFiles.filter(p => p.assetId === Number(this.selectedPlayer))[0]);

    this.autoBidService.startBid(this.level, lists, this.globals.currentAccount.id, selectedPlayerRequest, this.bidCriteriaModal.bidCriteria).subscribe(() => {
      this.alert.show('Done bidding', {cssClass: 'success'});
      this.autoBidStarted = false;
    }, err => {
      this.alert.show('Error while bidding: ' + err.message, {cssClass: 'danger'});
      console.error('Error in auto bidding ' + JSON.stringify(err));
      this.autoBidStarted = false;
    });
  }

  /**
   * Stop the bid.
   */
  stopBid(): void {
    this.autoBidService.stop().subscribe(resp => {
      console.log('Stopping: ' + resp);
    }, error => {
      console.error(error);
      this.completeBidding();
    });
  }

  /**
   * Disconnect and unsubscribe from websocket (clean up essentially).
   */
  cleanupWs(): void {
    this.autoBidService.stopListening();
  }

  /**
   * Pause the bidding
   */
  pauseBid(): void {
    this.paused = !this.paused;
    this.autoBidService.pause().subscribe(resp => {
      console.log('Paused: ' + resp);
    }, error => {
      console.error(error);
    });
  }

  /**
   * Clear textarea
   */
  clear(): void {
    this.autoBidService.clearMsgs();
  }

  /**
   * Bidding complete.
   */
  completeBidding(): void {
    this.clear();
    this.cleanupWs();
  }

  /**
   * Get a player by assetId.
   *
   * @param {number} assetId
   * @returns {Player}
   */
  getPlayer(assetId: number): Player {
    return this.globals.allPlayers.filter(p => p.assetId === assetId)[0];
  }

  /**
   * Return width of search body. Doing this as a method because it's sometimes undefined.
   *  Get the total width of the container and possibly calculate any margins.
   * @returns {any}
   */
  getCardWidth() {
    if (this.searchResultsBody) {
      return (this.searchResultsBody.nativeElement.offsetWidth - (15 * this.cardsPerRow)) / this.cardsPerRow;
    }
  }

  /**
   * Get player name
   *
   * @param {Player} player Player.
   * @returns {string} Player name
   */
  getPlayerName(player: Player) {
    return this.globals.getPlayerName(player);
  }

  /**
   * Send new option.
   *
   * @param {number} option
   */
  sendNewValue(option: number): void {
    // Send new options.

    if (option === 99 && !this.newPriceInput) {
      this.makeAuctionResponse().forEach(aI => {
        if (aI.buyNowPrice < this.newPriceInput) {
          this.newPriceInput = aI.buyNowPrice;
        }
      });
    }

    if (option === 2 && this.newPriceInput < 200) {
      this.alert.show('Value too low!', {cssClass: 'danger'});
    } else {
      this.autoBidService.sendOptions(option, this.newPriceInput).subscribe(() => {
        if (option === 2) {
          this.alert.show('New price set.', {cssClass: 'success'});
        }
        $('#tooManyModal').modal('hide');
      }, err => {
        this.alert.show('Error sending new price: ' + err.message, {cssClass: 'danger'});
        console.log(err);
        console.error('Error sending new price:' + JSON.stringify(err));
      });
    }
  }

  /**
   * Get the height for the status textarea
   *
   */
  getHeightForStatusText(): void {
    let factor = this.statusTextContainer.nativeElement.offsetHeight / 26;
    factor = Math.floor(factor);
    this.statusTextRows = factor;
  }

  /**
   * Create text to display is text area
   */
  makeTextareaText(): string {
    return this.autoBidService.playerSearchStatusMsg.join('\n');
  }


  /**
   * Make auction response.
   */
  makeAuctionResponse(): any {
    if (this.autoBidService.playerAuctionResp) {
      // Can't spam any rest calls here, so need to trust it here.
      if (this.autoBidService.playerAuctionResp.credits) {
        this.globals.currentAccount.coins = this.autoBidService.playerAuctionResp.credits;
      }
      return this.autoBidService.playerAuctionResp.auctionInfo;
    } else {
      return undefined;
    }
  }

  /**
   * Show the bid criteria modal.
   */
  showBidCriteria(): void {
    this.bidCriteriaModal.showModal();
  }
}
