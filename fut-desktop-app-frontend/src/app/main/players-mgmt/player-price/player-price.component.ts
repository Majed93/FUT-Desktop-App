import {AfterViewChecked, Component, ElementRef, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {GlobalService} from '../../../globals/global.service';
import {PlayerPricesService} from './player-prices.service';
import {Player} from '../../../domain/player';
import {HttpClient} from '@angular/common/http';
import {AlertService} from '../../../alert';

/**
 * Player price component.
 */
@Component({
  moduleId: module.id,
  selector: 'app-player-price',
  templateUrl: 'player-price.component.html',
  styleUrls: ['player-price.component.scss']
})
export class PlayerPriceComponent implements OnInit, OnDestroy, AfterViewChecked {

  loggedIn: boolean;
  @Input() playerList: Player[];
  @Input() fileName: string;

  level = 'Any';
  searchingStarted = false;
  paused = false;

  @ViewChild('statusText') statusTextarea: ElementRef;
  @ViewChild('statusTextContainer') statusTextContainer: ElementRef;
  @ViewChild('searchResultsBody') searchResultsBody: ElementRef;
  @Input() cardsPerRow = 5;
  @Input() statusTextRows = 26;

  constructor(private playerPriceService: PlayerPricesService, private globals: GlobalService,
              private http: HttpClient, private alert: AlertService) {
  }

  ngAfterViewChecked(): void {
    setTimeout(() => {
      this.getHeightForStatusText();
    });
  }


  ngOnInit() {
    this.statusTextRows = 30;

    this.loggedIn = this.globals.loggedIn;

    // TODO: TAKE OUT ONCE NOT TESTING. *************************************
    // this.http.get('../../../../assets/tradePile.json').subscribe((resp: any) => {
    //   this.auctionResp = resp;
    // });
    // .map((response) => <any> response).subscribe((resp) => {
    // this.auctionResp = resp;
    // });
    // TODO: TAKE OUT ONCE NOT TESTING. *************************************
  }


  /**
   * Start auto search for lowest bin
   */
  startSearch() {
    this.clear();
    const fileArray = [];
    fileArray.push(this.fileName);
    this.searchingStarted = true;

    this.playerPriceService.startListening();

    this.playerPriceService.startSearch(this.playerList, this.level, fileArray, this.globals.currentAccount.id).subscribe(resp => {
        this.alert.show('Done price updates', {cssClass: 'success'});
        console.log('Searched: ' + resp);
        this.searchingStarted = false;
      },
      error => {
        this.searchingStarted = false;
        this.alert.show('Error: ' + error.message, {cssClass: 'danger'});
        console.error('Error: ' + JSON.stringify(error));
      });
  }

  /**
   * Stop auto search.
   */
  stopSearch(): void {
    this.playerPriceService.stop().subscribe(resp => {
      console.log('Stopping: ' + resp);
    }, error => {
      console.error(error);
      this.searchComplete();
    });
  }

  /**
   * Pause the search
   */
  pauseSearch(): void {
    this.paused = !this.paused;
    this.playerPriceService.pause().subscribe(resp => {
      console.log('Paused: ' + resp);
    }, error => {
      console.error(error);
    });
  }

  /**
   * On component being destroyed.
   */
  ngOnDestroy(): void {
    if (this.playerPriceService) {
      this.playerPriceService.stop().subscribe(resp => {
        console.log('Stopping: ' + resp);
      }, error => {
        console.error(error);
      });
      this.playerPriceService.stopListening();
    }
  }

  /**
   * Completed search
   */
  searchComplete(): void {
    this.playerPriceService.stopListening();
  }

  /**
   * Clear textarea
   */
  clear(): void {
    this.playerPriceService.clearMsgs();
  }

  /**
   * Create text to display is text area
   */
  makeTextareaText(): string {
    return this.playerPriceService.playerSearchStatusMsg.join('\n');
  }

  /**
   * Make auction response.
   */
  makeAuctionResponse(): any {
    if (this.playerPriceService.playerAuctionResp) {
      return this.playerPriceService.playerAuctionResp.auctionInfo;
    } else {
      return undefined;
    }
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
   */
  getCardWidth() {
    if (this.searchResultsBody) {
      return (this.searchResultsBody.nativeElement.offsetWidth - (15 * this.cardsPerRow)) / this.cardsPerRow;
    }
  }

  /**
   * Get the height for the status textarea
   *
   */
  getHeightForStatusText() {
    if (this.statusTextContainer) {
      let factor = this.statusTextContainer.nativeElement.offsetHeight / 26;
      factor = Math.floor(factor);
      this.statusTextRows = factor;
    }
  }
}
