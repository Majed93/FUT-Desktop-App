import {Component, ElementRef, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {GlobalService} from '../../globals/global.service';
import {UnassignedService} from './unassigned.service';
import {StatusModalComponent} from '../../status-modal/status-modal.component';
import {CacheService} from '../../services/cache.service';
import {Player} from '../../domain/player';
import {WatchListService} from '../watch-list/watch-list.service';
import {AlertService} from '../../alert';

@Component({
  moduleId: module.id,
  selector: 'app-unassigned',
  templateUrl: 'unassigned.component.html',
  styleUrls: ['unassigned.component.scss']
})
export class UnassignedComponent implements OnInit, OnDestroy {

  loading = true;
  unassignedPile: any;
  @Input() cardsPerRow = 7;
  @Input() listDurationForAll = 3600;

  /** Status received from the status queue on player search */
  status = [];

  pauseEndpoint = this.global.endpoint + this.global.PAUSELISTING_PATH;
  stopEndpoint = this.global.endpoint + this.global.STOPLISTING_PATH;
  @ViewChild(StatusModalComponent) modal: StatusModalComponent;
  @ViewChild('unassignedPileBody') private unassignedPileBody: ElementRef;
  disableCloseModal = true;

  /**
   * Constructor
   *
   * @param {UnassignedService} unassignedService
   * @param {GlobalService} global
   * @param cacheService the cache service
   * @param watchListService watch list service
   * @param alerts alert service
   */
  constructor(private unassignedService: UnassignedService, private global: GlobalService,
              private cacheService: CacheService, private watchListService: WatchListService,
              private alerts: AlertService) {
    // NOOP
  }

  /**
   * On init.
   */
  ngOnInit() {
    this.global.updateCoinCount();
    this.getUnassignedPile();

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
  }

  /**
   * Get unassigned pile
   */
  getUnassignedPile() {
    this.unassignedService.getUnassignedPile(this.global.currentAccount.id).subscribe(pile => {
      this.checkUnassigned(pile);
      this.loading = false;
    }, error => {
      console.error('Error getting unassigned pile: ' + error);
      this.loading = false;
    });
  }

  /**
   * Move won items to trade pile
   */
  moveToTrade(): void {
    this.loading = true;
    this.cleanupMessages();
    this.startWs();
    // Need to pass it parameter as an auction response not a auction info.
    this.watchListService.moveToTrade(this.global.currentAccount.id,
      this.unassignedPile,
      0, 'unassignedPile').subscribe(resp => {
      this.checkUnassigned(resp);
      this.alerts.show('Items moved to trade pile', {cssClass: 'success'});
      this.loading = false;
      this.cleanupModalsAndMessages();
      this.disableCloseModal = false;
    }, error => {
      this.loading = false;
      this.cleanupModalsAndMessages();
      this.disableCloseModal = false;
      console.error('Error moving items from unassigned pile. ', error);
      this.alerts.show('Error moving items from unassigned pile. ', {cssClass: 'danger'});
    });
  }

  /**
   * Auto move and list items
   */
  autoList(): void {
    this.disableCloseModal = true;
    this.cleanupMessages();
    this.startWs();

    this.watchListService.autoList(this.global.currentAccount.id,
      this.unassignedPile, 0, 'unassignedPile').subscribe(resp => {
      this.checkUnassigned(resp);
      this.alerts.show('Items moved and listed', {cssClass: 'success'});
      this.cleanupModalsAndMessages();
      this.disableCloseModal = false;

    }, error => {
      this.cleanupModalsAndMessages();
      this.disableCloseModal = false;
      console.error('Error listing items from unassigned pile. ', error);
      this.alerts.show('Error listing items from unassigned pile. ', {cssClass: 'danger'});
    });
  }

  /**
   * Disconnect and unsubscribe from websocket (clean up essentially).
   */
  cleanupModalsAndMessages(): void {
    this.modal.hideModal();
    this.cleanupMessages();
  }

  /**
   * CLena up messages
   */
  cleanupMessages(): void {
    this.status = [];
    this.unassignedService.clearMessages();
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
   * @returns Width of card
   */
  getCardWidth() {
    if (this.unassignedPile && this.unassignedPileBody) {
      return (this.unassignedPileBody.nativeElement.offsetWidth - (15 * this.cardsPerRow)) / this.cardsPerRow;
    }
  }

  /**
   * New trade pile received.
   *
   * @param newUnassignedPile
   */
  onNewUnassigned(newUnassignedPile: any): void {
    this.unassignedPile = newUnassignedPile;
  }

  /**
   * Cheeck unassigned pile.
   *
   * @param pile The pile.
   */
  checkUnassigned(pile): void {
    if (pile.auctionInfo && pile.auctionInfo.length > 0) {
      this.unassignedPile = pile;
    } else {
      this.unassignedPile = undefined;

    }
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
    this.unassignedService.startListening();
    this.status = this.unassignedService.getListenStatusMsg;
  }
}
