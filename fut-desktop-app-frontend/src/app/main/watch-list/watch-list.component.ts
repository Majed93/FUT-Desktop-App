import {Component, ElementRef, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {WatchListService} from './watch-list.service';
import {AlertService} from '../../alert';
import {GlobalService} from '../../globals/global.service';
import {Player} from '../../domain/player';
import {StatusModalComponent} from '../../status-modal/status-modal.component';
import {CacheService} from '../../services/cache.service';

@Component({
  moduleId: module.id,
  selector: 'app-watch-list',
  templateUrl: 'watch-list.component.html',
  styleUrls: ['watch-list.component.scss']
})
export class WatchListComponent implements OnInit, OnDestroy {

  watchList: string;
  wonItems = 0;
  expiredItems = 0;
  activeItems = 0;
  removableItems = 0;
  loading = true;
  @Input() removeOptions = true;
  @Input() cardsPerRow = 7;
  @ViewChild('watchListBody') private watchListBody: ElementRef;
  @Input() listDurationForAll = 3600;

  disableCloseModal = true;
  /** Status received from the status queue on player search */
  status = [];

  pauseEndpoint = this.global.endpoint + this.global.PAUSELISTING_PATH;
  stopEndpoint = this.global.endpoint + this.global.STOPLISTING_PATH;
  @ViewChild(StatusModalComponent) modal: StatusModalComponent;

  /**
   * Constructor.
   */
  constructor(private watchListService: WatchListService, private global: GlobalService,
              private alerts: AlertService, private cacheService: CacheService) {
    // NOOP
  }

  /**
   * On init
   */
  ngOnInit(): void {
    this.getWatchList();
  }

  /**
   * On Destroy
   */
  ngOnDestroy(): void {
    if (this.cacheService) {
      this.cacheService.stopCache().subscribe(() => {
      }, err => {
        console.error('Error cleaning cache. ', err);
      });
    }
    this.watchListService.stopListening();
    this.cleanUpModalAndMsgs();
  }

  /**
   * Get watch list.
   */
  getWatchList() {
    this.loading = true;
    this.watchListService.getWatchList(this.global.currentAccount.id).subscribe(resp => {
      this.watchList = resp;
      this.watchListChecks();
      this.global.updateCoinCount();
    }, error => {
      console.error('Error getting watch list: ' + error);
      this.loading = false;
    });
  }

  /**
   * Move won items to trade pile
   */
  moveToTrade() {
    this.disableCloseModal = true;
    this.clearMsgs();
    this.startWs();

    this.watchListService.moveToTrade(this.global.currentAccount.id, this.watchList, 0, 'watchList').subscribe(resp => {
      this.watchList = resp;
      this.alerts.show('Items moved from watch list to trade pile', {cssClass: 'success'});
      this.watchListChecks();
      this.disableCloseModal = false;
      this.cleanUpModalAndMsgs();
    }, error => {
      this.cleanUpModalAndMsgs();
      this.disableCloseModal = false;
      console.log('Error moving items from watch list: ', error);
      this.alerts.show('Error moving items from watch list.', {cssClass: 'danger'});
    });
  }

  /**
   * Auto move and list items
   */
  autoList(): void {
    this.disableCloseModal = true;
    this.clearMsgs();
    this.startWs();

    this.watchListService.autoList(this.global.currentAccount.id,
      this.watchList, 0, 'watchList').subscribe(resp => {
      this.watchList = resp;
      this.alerts.show('Items moved and listed', {cssClass: 'success'});
      this.watchListChecks();
      this.disableCloseModal = false;
      this.cleanUpModalAndMsgs();
    }, error => {
      this.disableCloseModal = false;
      this.cleanUpModalAndMsgs();
      console.error('Error listing items from watchList. ', error);
      this.alerts.show('Error listing items from watchList. ', {cssClass: 'danger'});
    });
  }

  /**
   * Clean up the modal and messages
   */
  cleanUpModalAndMsgs(): void {
    this.modal.hideModal();
    this.clearMsgs();
  }

  /**
   * Remove expired or outbidded items
   */
  removeExpiredOutbidded() {
    this.watchListService.removeExpiredOutbidded(this.global.currentAccount.id, this.watchList, this.removeOptions).subscribe(resp => {
      this.watchList = resp;
      this.alerts.show('Items removed from watch list', {cssClass: 'success'});
      this.watchListChecks();
    }, error => {
      console.log('Error removing items from watch list: ' + JSON.stringify(error));
      this.alerts.show('Error removing items from watch list', {cssClass: 'danger'});
    });
  }

  /**
   * Do watch list checks
   */
  watchListChecks() {
    this.wonItems = 0;
    this.expiredItems = 0;
    this.activeItems = 0;
    this.removableItems = 0;

    if (this.watchList) {
      if (!this.watchList['auctionInfo'] || this.watchList['auctionInfo'].length < 1) {
        // disable all since we can't really do anything.
      } else {
        this.watchList['auctionInfo'].filter(a => {
          if (a.tradeState === 'expired' || (a.tradeState === 'closed' && a.bidState === 'outbid') || a.itemData.itemState === 'invalid') {
            this.expiredItems++;
            this.removableItems++;
          } else if (a.currentBid > 0 && a.tradeState === 'closed') {
            this.wonItems++;
          } else if (a.tradeState === 'active' && a.bidState === 'outbid') {
            this.removableItems++;
          } else {
            this.activeItems++;
          }
        });
      }
    }

    this.loading = false;
  }

  /**
   * Get a player by assetId.
   *
   * @param {number} assetId
   * @returns {Player}
   */
  getPlayer(assetId: number): Player {
    const player: Player = this.global.allPlayers.filter(p => p['assetId'] === assetId)[0];
    return player === undefined ? null : player;
  }

  /**
   * Return width of search body. Doing this as a method because it's sometimes undefined.
   *  Get the total width of the container and possibly calculate any margins.
   * @returns {any}
   */
  getCardWidth() {
    if (this.watchList && this.watchListBody) {
      return (this.watchListBody.nativeElement.offsetWidth - (15 * this.cardsPerRow)) / this.cardsPerRow;
    }
  }

  /**
   * New watch list received.
   *
   * @param newWatchList
   */
  onNewWatchList(newWatchList: any): void {
    this.watchList = newWatchList;
    this.watchListChecks();
  }

  /**
   * Clear messages
   */
  clearMsgs(): void {
    this.status = [];
    this.watchListService.clearMessages();
  }

  /**
   * Create text to display is text area
   */
  makeTextareaText(): string {
    return this.status.join('\n');
  }

  /**
   * Start listening to the websocket.
   */
  startWs(): void {
    // Connect to web socket
    this.watchListService.startListening();
    this.status = this.watchListService.getListenStatusMsg;
  }
}
