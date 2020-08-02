import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {TradePileService} from './trade-pile.service';
import {GlobalService} from '../../globals/global.service';
import {Player} from '../../domain/player';
import {AlertService} from 'app/alert';
import {StatusModalComponent} from '../../status-modal/status-modal.component';
import {CacheService} from '../../services/cache.service';

declare const jquery: any;
declare const $: any;

@Component({
  moduleId: module.id,
  selector: 'app-trade-pile',
  templateUrl: 'trade-pile.component.html',
  styleUrls: ['trade-pile.component.scss']
})
export class TradePileComponent implements OnInit, OnDestroy {

  tradePile: any;
  soldItems = 0;
  @ViewChild('tradePileBody') private tradePileBody: ElementRef;
  @Input() cardsPerRow = 7;
  @Input() listDurationForAll = 3600;
  loading = true;
  disableRelistAllBtn = false;
  disableAutolistUnlistedBtn = false;
  @Input() newPrice = false;

  /** Status received from the status queue on player search */
  status = [];

  pauseEndpoint = this.global.endpoint + this.global.PAUSELISTING_PATH;
  stopEndpoint = this.global.endpoint + this.global.STOPLISTING_PATH;
  @ViewChild(StatusModalComponent) modal: StatusModalComponent;
  disableCloseModal: boolean;

  /**
   * Constructor.
   * @param {TradePileService} tradePileService
   * @param {GlobalService} global
   * @param alerts Alert service
   * @param cacheService the Cache service
   */
  constructor(private tradePileService: TradePileService, private global: GlobalService,
              private alerts: AlertService, private cacheService: CacheService) {
    // NOOP
  }

  /**
   * On initialization of component.
   */
  ngOnInit(): void {
    this.getTradePile();

    this.disableCloseModal = true;
  }

  /**
   * On destroy.
   */
  ngOnDestroy(): void {
    if (this.cacheService) {
      this.cacheService.stopCache().subscribe(() => {
      }, err => {
        console.error('Error stopping cache. ', err);
      });
    }
    this.cleanupModalAndMessages();
  }

  /**
   * Get tradepile.
   */
  getTradePile(): void {
    this.loading = true;
    this.tradePileService.getTradePile(this.global.currentAccount.id).subscribe(resp => {
      this.tradePile = resp;
      this.tradePileChecks();
      this.global.updateCoinCount();
    }, error => {
      console.error('Error getting trade pile: ' + error);
      this.loading = false;
    });
  }

  /**
   * Remove sold items.
   *
   */
  removeSold(): void {
    this.loading = true;
    this.tradePileService.removeSold(this.global.currentAccount.id).subscribe(resp => {
      this.tradePile = resp;
      this.tradePileChecks();
    }, error => {
      console.error('Error getting trade pile: ' + error);
      this.loading = false;
    });
  }

  /**
   * Re-list items.
   *
   * @param {boolean} reListAll If true then relist all for same, if false then relist all for specified amount of time.
   */
  reList(reListAll: boolean): void {
    this.clear();
    // Connect to web socket
    this.tradePileService.startListening();
    this.status = this.tradePileService.getListenStatusMsg;

    this.disableCloseModal = true;
    // TODO: Need to disable EVERYTHING if false so the user can't click on anything.

    if (reListAll) {
      this.tradePileService.reList(this.global.currentAccount.id, reListAll, this.listDurationForAll,
        this.tradePile, this.newPrice).subscribe(resp => {
        this.tradePile = resp;
        this.disableCloseModal = false;
        this.tradePileChecks();
      }, error => {
        console.error('Error listing: ' + error);
        this.disableCloseModal = false;

      });
    } else {
      // Relist one item at a time.
      this.tradePileService.reList(this.global.currentAccount.id, reListAll, this.listDurationForAll,
        this.tradePile, this.newPrice).subscribe(resp => {
        this.tradePile = resp;
        this.alerts.show('Re listed successfully', {cssClass: 'success'});
        this.disableCloseModal = false;
        this.tradePileChecks();
      }, error => {
        console.error('Error listing: ' + error);
        this.disableCloseModal = false;
        this.alerts.show('Error listing.', {cssClass: 'danger'});
      });
    }
  }

  /**
   * Auto list unlisted items.
   *
   */
  autoListUnlisted() {
    this.clear();
    this.disableCloseModal = true;
    // Connect to web socket
    this.tradePileService.startListening();
    this.status = this.tradePileService.getListenStatusMsg;

    // TODO: Need to disable EVERYTHING if false so the user can't click on anything.


    // Relist one item at a time.
    this.tradePileService.autoListUnlisted(this.global.currentAccount.id, this.listDurationForAll,
      this.tradePile, this.newPrice).subscribe(resp => {
      this.tradePile = resp;
      this.alerts.show('Listed successfully', {cssClass: 'success'});
      this.disableCloseModal = false;
      this.tradePileChecks();
    }, error => {
      console.error('Error listing: ' + error);
      this.disableCloseModal = false;
      this.alerts.show('Error listing.', {cssClass: 'danger'});
    });
  }

  /**
   * Disconnect and unsubscribe from websocket (clean up essentially).
   */
  cleanupModalAndMessages() {
    this.tradePileService.stopListening();
    this.clear();
    this.modal.hideModal();
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
   * @returns Card width.
   */
  getCardWidth() {
    if (this.tradePile && this.tradePileBody) {
      return (this.tradePileBody.nativeElement.offsetWidth - (15 * this.cardsPerRow)) / this.cardsPerRow;
    } else {
      return 0;
    }
  }

  /**
   * Do trade pile checks.
   */
  tradePileChecks() {
    this.disableRelistAllBtn = true;
    this.disableAutolistUnlistedBtn = true;
    this.soldItems = 0;
    if (this.tradePile) {

      if (this.tradePile['auctionInfo'].length < 1) {
        // disable all since we can't really do anything.

      } else {
        this.tradePile['auctionInfo'].filter(a => {
          if (a.currentBid > 0 && a.tradeState === 'closed') {
            this.soldItems++;
          } else if (a.expires < 1 && a.tradeState === 'expired') {
            this.disableRelistAllBtn = false;
          }

          if (a.tradeState === null) {
            this.disableAutolistUnlistedBtn = false;
          }
        });
      }
    }

    this.loading = false;
  }

  /**
   * Clear text box and status.
   */
  clear(): void {
    this.status = [];
    this.tradePileService.clearMessages();
  }

  /**
   * New trade pile received.
   *
   * @param newTradePile
   */
  onNewTradePile(newTradePile: any): void {
    this.tradePile = newTradePile;
    this.tradePileChecks();
  }

  /**
   * Create text to display is text area
   */
  makeTextareaText(): string {
    return this.status.join('\n');
  }
}
