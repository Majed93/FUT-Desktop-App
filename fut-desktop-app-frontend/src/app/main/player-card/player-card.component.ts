import {Component, ElementRef, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {Player} from '../../domain/player';
import {GlobalService} from '../../globals/global.service';
import {ChemistryStyle} from '../../enums/chemistryStyle';
import * as moment from 'moment';
import {AlertService} from '../../alert/alert.service';
import {TradePileService} from '../trade-pile/trade-pile.service';
import {WatchListService} from '../watch-list/watch-list.service';
import {SearchService} from '../transfer-search/search.service';
import {Resources} from '../../const/resources.const';

declare let jquery: any;
declare let $: any;

@Component({
  moduleId: module.id,
  selector: 'app-player-card',
  templateUrl: 'player-card.component.html',
  styleUrls: ['player-card.component.scss']
})
export class PlayerCardComponent implements OnInit, OnDestroy {

  /**
   * Array of auction info for player.
   */
  loading = false;
  @Input() auctionInfo;
  @Input() player: Player;
  @Input() itemWidth: any;
  @Input() listType: string;
  /* NOTE: ONLY FOR WATCH LIST */
  @Input() listResp;
  timer: any;
  timeLeft: number;
  timeLeftDate: any;
  currentTime: any;
  finishTime: any;
  diffTime: any;
  initialized = false;
  color = '';
  cardStatus = 'Active';
  playerImg: string;
  nationImg: string;
  clubImg: string;
  cardType = '';
  rating: number;
  untradable: boolean;

  @Input() listDuration: number;
  @Input() startPrice;
  @Input() buyNowPrice;
  @Input() bidAmount;
  @Output() listUpdated: EventEmitter<any> = new EventEmitter<any>();
  @Output() itemUpdated: EventEmitter<any> = new EventEmitter<any>();
  @ViewChild('moreModalClose') moreModal: ElementRef;

  /**
   * Constructor.
   *
   * @param {GlobalService} globals
   * @param tradePileService
   * @param watchListService
   * @param alerts
   * @param searchService
   */
  constructor(private globals: GlobalService, private tradePileService: TradePileService,
              private watchListService: WatchListService, private alerts: AlertService,
              private searchService: SearchService) {
  }

  ngOnInit() {
    // Get images if it's a player.
    this.cardType = this.auctionInfo.itemData.itemType;
    this.getImgs();

    // Set the colors
    this.cardState();

    this.rating = this.cardType === 'player' ? this.auctionInfo.itemData.rating : '';

    this.timeLeft = this.auctionInfo.expires;
    this.currentTime = moment(0).valueOf();
    this.finishTime = this.auctionInfo.expires * 1000;
    this.diffTime = this.finishTime - this.currentTime;
    let duration: any = moment.duration(this.diffTime, 'milliseconds');

    this.startPrice = this.auctionInfo.startingBid;
    this.buyNowPrice = this.auctionInfo.buyNowPrice;
    this.untradable = this.auctionInfo.itemData.untradeable;
    this.bidAmount = this.auctionInfo.currentBid < 150 ?
      this.auctionInfo.startingBid : this.globals.nextBidAmount(this.auctionInfo.currentBid);
    this.timer = setInterval(() => {
      this.timeLeft--;

      if (this.timeLeft < 1) {
        if (this.auctionInfo.currentBid > 0) {
          this.auctionInfo.tradeState = 'closed';
        } else if (this.auctionInfo.tradeState != null) {
          this.auctionInfo.tradeState = 'expired';
        }
        this.cardState();
        this.timeLeftDate = 'expired';
        clearInterval(this.timer);
      } else {
        if (this.timeLeft < 15) {
          if (this.cardStatus === 'Outbidded') {
            this.color = 'flash-orange';
          } else if (this.cardStatus === 'Winning') {
            this.color = 'flash-green';
          }
        }
        duration = moment.duration((duration - 1000), 'milliseconds');

        if (!this.initialized) {
          this.initialized = true;
        }

        const days = duration.days() < 10 ? '0' + duration.days() : duration.days();
        const hrs = duration.hours() < 10 ? '0' + duration.hours() : duration.hours();
        const mins = duration.minutes() < 10 ? '0' + duration.minutes() : duration.minutes();
        const secs = duration.seconds() < 10 ? '0' + duration.seconds() : duration.seconds();

        this.timeLeftDate = days + ':' + hrs + ':' + mins + ':' + secs;
      }
    }, 1000);
  }

  ngOnDestroy(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }

  /**
   * Get player name
   *
   * @returns {string}
   */
  getName() {
    if (this.player && this.cardType === 'player') {
      return this.globals.getPlayerName(this.player);
    } else {
      return this.cardType;
    }
  }

  /**
   * Get chemistry style
   *
   * @param {number} id Id of enum
   * @returns {any} Chemistry style string.
   */
  getChemistry(id: number) {
    if (this.cardType === 'player') {
      let chem = ChemistryStyle[id];

      if (chem === 'GkBasic') {
        chem = 'GK Basic';
      }

      return chem;
    } else {
      return 'N/A';
    }
  }

  /**
   * Get item images.
   */
  getImgs() {
    if (this.cardType === 'player') {
      if (this.auctionInfo.itemData.assetId && this.auctionInfo.itemData.assetId !== '') {
        this.playerImg = 'https://www.easports.com/fifa/ultimate-team/web-app/content/' +
          `${Resources.ASSET_ID}/20${Resources.YEAR}/fut/items/images/mobile/portraits/` +
          this.auctionInfo.itemData.assetId + '.png';
      }
      if (this.auctionInfo.itemData.teamId && this.auctionInfo.itemData.teamId !== '') {
        this.clubImg = 'https://www.easports.com/fifa/ultimate-team/web-app/content/' +
          `${Resources.ASSET_ID}/20${Resources.YEAR}/fut/items/images/mobile/clubs/normal/` +
          this.auctionInfo.itemData.teamId + '.png';
      }
      if (this.auctionInfo.itemData.nation && this.auctionInfo.itemData.nation !== '') {
        this.nationImg = 'https://www.easports.com/fifa/ultimate-team/web-app/content/' +
          `${Resources.ASSET_ID}/20${Resources.YEAR}/fut/items/images/mobile/flags/card/` +
          this.auctionInfo.itemData.nation + '.png';
      }
    }
  }

  /**
   * TODO: Explain this.
   */
  cardState() {
    if (this.isExpired()) {
      this.color = 'danger';
      this.cardStatus = 'Expired';
    } else if (this.auctionInfo.tradeState === 'closed' && this.auctionInfo.currentBid > 0) {
      this.color = 'success';
      if (this.listType === 'tradePile') {
        this.cardStatus = 'Sold';
      } else if ((this.listType === 'watchList' && this.auctionInfo.bidState === 'highest')
        || (this.listType === 'search' && this.auctionInfo.bidState === 'buyNow')) {
        this.cardStatus = 'Won';
      }
    } else if (this.auctionInfo.tradeState === 'active' && this.auctionInfo.bidState === 'highest') {
      this.color = 'light-green';
      this.cardStatus = 'Winning';
    } else if (this.auctionInfo.tradeState === 'active' && this.auctionInfo.bidState === 'outbid') {
      this.color = 'orange';
      this.cardStatus = 'Outbidded';
    } else if (this.auctionInfo.tradeState == null) {
      this.cardStatus = 'Unlisted';
    }
  }

  /**
   * Check if card is expired or not.
   *
   * @returns {boolean}
   */
  isExpired(): boolean {
    if (this.auctionInfo.tradeState === 'closed' && this.auctionInfo.bidState === 'outbid') {
      return true;
    }
    if (this.listType === 'watchList' && this.auctionInfo.bidState !== 'highest' &&
      (this.auctionInfo.tradeState === 'closed' || this.auctionInfo.tradeState === 'expired')) {
      return true;
    }
    if ((this.listType === 'search' || this.listType === 'market') &&
      (this.auctionInfo.bidState !== 'highest' && this.auctionInfo.bidState !== 'buyNow') &&
      (this.auctionInfo.tradeState === 'closed' || this.auctionInfo.tradeState === 'expired')) {
      return true;
    }

    return this.listType === 'tradePile' && this.auctionInfo.tradeState === 'expired';
  }

  /**
   * Send item back to club.
   */
  sendBackToClub() {
// TODO
  }

  /**
   * Quick sell item.
   */
  quickSell() {
// TODO
  }

  /**
   * Remove sold item. ONLY FOR TRADE PILE
   */
  removeSold() {
// TODO
  }

  /**
   * Remove expired from watch list. ONLY FOR WATCH LIST.
   */
  removeItemFromWatchList() {
    this.loading = true;

    this.watchListService.removeItemFromWatchList(this.getCurrentAccountId(), this.auctionInfo.tradeId).subscribe(resp => {
      this.listUpdated.emit(resp);
      this.alerts.show('Item removed from watch list', {cssClass: 'success'});
      this.moreModal.nativeElement.click();
      this.loading = false;
    }, error => {
      console.log('Error removing item from watch list.: ' + JSON.stringify(error));
      this.alerts.show('Error removing item from watch list.', {cssClass: 'danger'});
      this.loading = false;
    });
  }

  /**
   * List item.
   */
  listItem() {
    this.loading = true;
    const auctionDetails = {
      itemDataId: this.auctionInfo.itemData.id,
      auctionDuration: this.listDuration,
      startingBid: this.startPrice,
      buyNowPrice: this.buyNowPrice,
    };

    this.tradePileService.listItem(this.getCurrentAccountId(), auctionDetails, this.listType, this.listResp).subscribe(resp => {
      this.listUpdated.emit(resp); // Tell the trade pile that it's been updated.
      this.alerts.show('Item listed', {cssClass: 'success'});
      this.moreModal.nativeElement.click();
      this.loading = false;
    }, error => {
      console.log('Error listing item: ' + JSON.stringify(error));
      this.alerts.show('Error listing item.', {cssClass: 'danger'});
      this.loading = false;
    });
  }

  /**
   * Send to trade pile.
   * NOTE: ONLY FOR WATCH LIST AND UNASSIGNED
   */
  sendToTradePile() {
    this.loading = true;
    this.watchListService.moveToTrade(this.getCurrentAccountId(),
      this.listResp,
      this.listType === 'watchList' ? this.auctionInfo.tradeId : this.auctionInfo.itemData.id
      , this.listType).subscribe(resp => {
      this.listUpdated.emit(resp);
      this.alerts.show('Item moved', {cssClass: 'success'});
      this.moreModal.nativeElement.click();
      this.loading = false;
    }, error => {
      console.log('Error moving item to trade : ', error);
      this.alerts.show('Error moving item to trade pile.' + error.message, {cssClass: 'danger'});
      this.loading = false;
    });
  }

  /**
   * Bid on item.
   */
  bidItem() {
    this.loading = true;
    this.searchService.bid(this.globals.currentAccount.id, this.auctionInfo, this.bidAmount).subscribe(resp => {
      console.log(resp);
      // emit this item to update the list.
      this.itemUpdated.emit(resp);
      this.moreModal.nativeElement.click();
      this.loading = false;
      this.alerts.show('Bidded', {cssClass: 'success'});
    }, err => {
      console.log('Error bidding : ' + err);
      this.loading = false;
      this.alerts.show('Error bidding.' + err.message, {cssClass: 'danger'});
      this.moreModal.nativeElement.click();
    });
  }

  /**
   * BIN Item.
   */
  binItem() {
    this.loading = true;
    this.searchService.bin(this.globals.currentAccount.id, this.auctionInfo).subscribe(resp => {
      // emit this item to update the list.
      this.itemUpdated.emit(resp);
      this.alerts.show('BINNED', {cssClass: 'success'});
      this.loading = false;
      this.moreModal.nativeElement.click();
    }, err => {
      console.log('Error BINning : ' + err);
      this.loading = false;
      this.alerts.show('Error BINing.' + err.message, {cssClass: 'danger'});
      this.moreModal.nativeElement.click();
    });
  }

  /**
   * Get current account id.
   *
   * @returns {number}
   */
  getCurrentAccountId(): number {
    return this.globals.currentAccount.id;
  }

  /**
   * Get modal id
   *
   * @returns {string}
   */
  getModalId(): string {
    let modalId = 'moreModal-';
    if (this.listType === 'unassignedPile') {
      modalId += this.auctionInfo.itemData.id;
    } else {
      modalId += this.auctionInfo.tradeId;
    }
    return modalId;
  }
}
