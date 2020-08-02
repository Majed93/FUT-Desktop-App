import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {Player} from '../../domain/player';
import {AlertService} from '../../alert';
import {GlobalService} from '../../globals/global.service';
import {FormBuilder, FormGroup} from '@angular/forms';
import {SearchService} from './search.service';
import {InputFiltersComponent} from '../../input-filters/input-filters.component';

@Component({
  moduleId: module.id,
  selector: 'app-transfer-search',
  templateUrl: 'transfer-search.component.html',
  styleUrls: ['transfer-search.component.scss']
})
export class TransferSearchComponent implements OnInit {

  auctionResp: any;

  @Input() cardsPerRow = 5;
  @ViewChild('searchResultsBody') private searchResultsBody: ElementRef;
  marketSearchForm: FormGroup;
  selectedPlayer: Player;
  allPlayers: Player[];
  firstSearch: boolean;
  loading = false;
  @ViewChild('playerInput') playerInput: InputFiltersComponent;

  /**
   * Constructor
   * @param {GlobalService} globals
   * @param {AlertService} alert
   * @param {FormBuilder} fb
   * @param {SearchService} searchService
   */
  constructor(private globals: GlobalService, private alert: AlertService,
              private fb: FormBuilder, private searchService: SearchService) {
  }

  ngOnInit() {
    this.globals.updateCoinCount();
    this.allPlayers = this.globals.allPlayers;
    this.createForm();
    this.firstSearch = true;
  }

  /**
   * Ensure the assetId is persisted.
   *
   * @param {Event} event
   */
  formChange(event: Event) {
    if (this.selectedPlayer) {
      this.marketSearchForm.value.assetId = this.selectedPlayer.assetId;
    }
  }

  /**
   * Create the form.
   */
  createForm() {
    this.marketSearchForm = this.fb.group({
      accountId: [this.globals.currentAccount.id],
      itemType: ['player'], // TODO: make dropdown or w/e
      level: ['Any'],
      assetId: [''],
      minBid: [''],
      maxBid: [''],
      minBuy: [''],
      maxBuy: ['']
    });

    this.playerInput.reset();
  }

  /**
   * Select player from search box.
   *
   * @param {Player} player
   */
  playerSelectionChange(player: Player) {
    this.selectedPlayer = player;
    this.marketSearchForm.value.assetId = this.selectedPlayer.assetId;
  }

  search() {
    this.loading = true;
    this.searchService.search(this.marketSearchForm.value, this.firstSearch).subscribe(resp => {
      this.auctionResp = resp;
      this.firstSearch = false;
      this.loading = false;
    }, err => {
      this.loading = false;
      this.alert.show('Error searching: ' + err.message, {cssClass: 'danger'});
      console.log('Error searching: ' + JSON.stringify(err));
    });
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

  onUpdatedItem(newItem: any): void {
    // loop through current list and update the item.
    const item = newItem.auctionInfo[0];

    let counter = 0;
    // debugger;
    this.auctionResp.auctionInfo.forEach(a => {
      if (a.tradeId === item.tradeId) {
        this.auctionResp.auctionInfo[counter] = item;
        // return;
      }
      counter++;
    });
  }
}
