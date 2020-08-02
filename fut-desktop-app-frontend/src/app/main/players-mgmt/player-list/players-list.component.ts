import {Component, ElementRef, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {PlayerListService} from './player-list.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Player} from '../../../domain/player';
import {AlertService} from '../../../alert';
import {GlobalService} from '../../../globals/global.service';
import {ProfitMargin} from '../../../domain/profitMargin';
import {InputFiltersComponent} from '../../../input-filters/input-filters.component';

@Component({
  moduleId: module.id,
  selector: 'app-players-list',
  templateUrl: 'players-list.component.html',
  styleUrls: ['players-list.component.scss']
})
export class PlayerListComponent implements OnInit, OnChanges {

  @Input() selectedFile: string;
  // For searching in player list table
  @Input() searchTable: string;
  @Input() playerList: Player[];
  @Input() minBid = 300;
  @Input() page = 1;
  maxSize = 10;

  @Input() minBidStep = 50;

  playerAddForm: FormGroup;
  playerUpdateForm: FormGroup;
  playerListMsg: string;

  order: string;
  desc = false;

  // Arrays for add player form ************

  allPlayers: Player[];
  allTeams = [];
  allNations = [];
  allLeagues = [];
  allPositions = [];

  @ViewChild('playerInput') playerInput: InputFiltersComponent;
  @ViewChild('positionInput') positionInput: InputFiltersComponent;
  @ViewChild('nationInput') nationInput: InputFiltersComponent;
  @ViewChild('clubInput') clubInput: InputFiltersComponent;
  @ViewChild('leagueInput') leagueInput: InputFiltersComponent;

  // ***************************************

  @ViewChild('addFormModalClose') addFormModalClose: ElementRef;
  @ViewChild('viewUpdateFormModalClose') viewUpdateFormModalClose: ElementRef;
  @ViewChild('deleteFormModalClose') deleteFormModalClose: ElementRef;
  @ViewChild('profitMarginModalClose') profitMarginModalClose: ElementRef;
  @ViewChild('profitModalClose') profitModalClose: ElementRef;

  profitMarginsFilters: Array<ProfitMargin> = [];

  constructor(private playerListService: PlayerListService, private fb: FormBuilder,
              private alertService: AlertService, private globals: GlobalService) {
  }

  /**
   * On init.
   */
  ngOnInit() {
    this.allPlayers = this.globals.allPlayers;
    this.allTeams = this.globals.allTeams;
    this.allNations = this.globals.allNations;
    this.allLeagues = this.globals.allLeagues;
    this.allPositions = this.globals.allPositions;

    this.createProfitMarginFilters();
    this.createForm();
    this.onChange();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.onBidInputChange();
  }

  createProfitMarginFilters() {
    this.profitMarginsFilters.push(this.generateFilters(1000, 300, 500, 0, 900));
    this.profitMarginsFilters.push(this.generateFilters(1500, 500, 750, 0, 1400));
    this.profitMarginsFilters.push(this.generateFilters(2000, 800, 900, 0, 1900));
    this.profitMarginsFilters.push(this.generateFilters(5000, 900, 1000, 0, 4900));
    this.profitMarginsFilters.push(this.generateFilters(10000, 1200, 1500, 0, 9900));
    this.profitMarginsFilters.push(this.generateFilters(20000, 1900, 2100, 0, 19000));
    this.profitMarginsFilters.push(this.generateFilters(30000, 2900, 3100, 0, 29000));
    this.profitMarginsFilters.push(this.generateFilters(40000, 3900, 4100, 0, 39000));
    this.profitMarginsFilters.push(this.generateFilters(50000, 4900, 5100, 0, 49000));
    this.profitMarginsFilters.push(this.generateFilters(100000, 8000, 9000, 0, 99000));
    this.profitMarginsFilters.push(this.generateFilters(15000000, 99000, 100000, 0, 999000));
  }

  /**
   * Helper to generate filters.
   * @param key Apply filter for under this price.
   * @param min Min profit.
   * @param max Max profit.
   * @param price Price set choice value.
   * @param egB Example Bin.
   * @returns {ProfitMargin} created filter
   */
  generateFilters(key, min, max, price, egB): ProfitMargin {
    const customFilter = new ProfitMargin();

    customFilter.key = key;
    customFilter.minProfit = min;
    customFilter.maxProfit = max;
    customFilter.priceSetChoice = price;
    customFilter.exampleBin = egB;

    return customFilter;
  }

  /**
   * Remove filter
   * @param {ProfitMargin} pm Filter to remove.
   */
  removeFilter(pm: ProfitMargin) {
    this.profitMarginsFilters = this.profitMarginsFilters.filter(p => p !== pm);
  }

  /**
   * Add a default filter.
   */
  addFilter() {
    const filter = new ProfitMargin();

    filter.key = 5000;
    filter.minProfit = 1000;
    filter.maxProfit = 1500;
    filter.priceSetChoice = 1;
    filter.exampleBin = 5000;
    this.profitMarginsFilters.push(filter);
  }

  /**
   * Create forms
   */
  createForm() {
    // Forms Controls
    this.playerAddForm = this.fb.group({
      assetId: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      commonName: [''],
      rating: ['', Validators.required],
      position: ['', Validators.required],
      lowestBin: [0, Validators.required],
      searchPrice: [0, Validators.required],
      maxListPrice: [0, Validators.required],
      minListPrice: [0, Validators.required],
      nation: ['', Validators.required],
      club: ['', Validators.required],
      league: ['', Validators.required],
      customPrice: [''],
      priceSet: [''],
      bidAmount: [0, Validators.required]
    });

    // Forms Controls
    this.playerUpdateForm = this.fb.group({
      assetId: [''],
      firstName: [''],
      lastName: [''],
      commonName: [''],
      rating: [''],
      position: [''],
      lowestBin: ['', Validators.required],
      searchPrice: ['', Validators.required],
      maxListPrice: ['', Validators.required],
      minListPrice: ['', Validators.required],
      nation: [''],
      club: [''],
      league: [''],
      customPrice: [''],
      priceSet: [''],
      bidAmount: ['', Validators.required]
    });

    this.playerInput.reset();
    this.positionInput.reset();
    this.nationInput.reset();
    this.clubInput.reset();
    this.leagueInput.reset();
  }

  /**
   * On bid inputs change.
   */
  onBidInputChange() {
    this.minBid = this.globals.roundToNearest(this.minBid);
    this.minBidStep = this.globals.stepValue(this.minBid);
  }

  onChange() {
    this.playerListService.getPlayerList(this.selectedFile).subscribe(resp => {
      this.playerList = resp;
      this.playerList.forEach(p => {
        p.minListPriceTax = (p.minListPrice * 0.95) - p.bidAmount;
        p.maxListPriceTax = (p.maxListPrice * 0.95) - p.bidAmount;
      });
    }, error => {
      console.error('Error getting player list: ' + JSON.stringify(error));
    });
  }

  rowClick(player: Player) {
    // Forms Controls
    this.playerUpdateForm = this.fb.group({
      assetId: [player.assetId],
      firstName: [player.firstName],
      lastName: [player.lastName],
      commonName: [player.commonName],
      rating: [player.rating],
      position: [player.position],
      lowestBin: [player.lowestBin, Validators.required],
      searchPrice: [player.searchPrice, Validators.required],
      maxListPrice: [player.maxListPrice, Validators.required],
      minListPrice: [player.minListPrice, Validators.required],
      nation: [player.nation],
      club: [player.club],
      league: [player.league],
      customPrice: [player.customPrice],
      priceSet: [player.priceSet],
      bidAmount: [player.bidAmount, Validators.required]
    });
  }

  /**
   * Add player to list.
   */
  onAddForm() {
    const formModel = this.playerAddForm.value;
    this.playerListService.addPlayer(this.selectedFile, formModel).subscribe(resp => {
      this.playerList = resp;
      this.playerListMsg = 'Added player';
      this.alertService.show(this.playerListMsg, {cssClass: 'success'});
      this.onChange();
      this.closeModalsAndResetMsgs();
      this.createForm();
      setTimeout(() => {
        this.playerListMsg = '';
      }, 3000);
    }, error => {
      this.playerListMsg = 'Error adding: ' + error.error.message;
      this.alertService.show(this.playerListMsg, {cssClass: 'danger'});
      console.error('Error getting player list: ', error);
    });
  }

  /**
   * Update player
   */
  onUpdateForm() {
    const formModel = this.playerUpdateForm.value;
    this.playerListService.updatePlayer(this.selectedFile, formModel).subscribe(resp => {
      this.playerList = resp;
      this.playerListMsg = 'Updated player';
      this.alertService.show(this.playerListMsg, {cssClass: 'success'});
      this.onChange();
      setTimeout(() => {
        this.playerListMsg = '';
      }, 4000); // Same as alert service time
    }, error => {
      this.playerListMsg = 'Error updating: ' + error.error.message;
      this.alertService.show(this.playerListMsg, {cssClass: 'danger'});
      console.error('Error getting player list: ', error);

    });
  }

  onDelete() {
    const formModel = this.playerUpdateForm.value;
    this.playerListService.deletePlayer(this.selectedFile, formModel.assetId).subscribe(() => {
      this.closeModalsAndResetMsgs();
      this.onChange();
      this.alertService.show('Deleted player', {cssClass: 'success'});
    }, error => {
      this.playerListMsg = 'Error deleting: ' + error.error.message;
      this.alertService.show(this.playerListMsg, {cssClass: 'danger'});
      console.error('Error deleting player: ', error);
      setTimeout(() => {
        this.playerListMsg = '';
      }, 3000);
    });
  }

  /**
   * Update Prices.
   */
  updatePrices() {
    this.onBidInputChange();
    this.playerListService.updatePrices(this.selectedFile, this.playerList,
      this.profitMarginsFilters, this.minBid).subscribe(resp => {
      if (resp) {
        this.onChange();
        this.alertService.show('Updated prices', {cssClass: 'success'});
        this.closeModalsAndResetMsgs();
      } else {
        this.alertService.show('Error updating prices', {cssClass: 'danger'})
      }
    }, error => {
      console.error('Error updating price: ' + JSON.stringify(error));
    });
  }

  /**
   * Set the 'priceSet' value on players all to be false.
   */
  resetPriceSet() {
    this.playerListService.resetPriceSet(this.selectedFile).subscribe(resp => {
      if (resp) {
        this.onChange();
        this.closeModalsAndResetMsgs();
        this.alertService.show('Updated prices set value for all players in current list.', {cssClass: 'success'});
      } else {
        this.alertService.show('Error resetting price set, try again later.', {cssClass: 'danger'});
      }
    }, error => {
      console.error('Error resetting priceSet: ' + JSON.stringify(error));
    });
  }

  /**
   * Close modals and reset msgs.
   */
  closeModalsAndResetMsgs() {
    this.addFormModalClose.nativeElement.click();
    this.viewUpdateFormModalClose.nativeElement.click();
    this.deleteFormModalClose.nativeElement.click();
    this.profitMarginModalClose.nativeElement.click();
    this.profitModalClose.nativeElement.click();
  }

  /**
   * Order by pipe
   *
   * @param {string} value
   */
  setOrder(value: string) {
    if (this.order === value) {
      this.desc = !this.desc;
    }

    this.order = value;
  }

  /**
   * Get nearest EA value
   *
   * @param {number} amount Amount to convert
   * @returns {number} Converted amount
   *
   */
  getNearestNormal(amount: number) {
    return this.globals.roundToNearest(amount);
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
   * Handle player change event.
   *
   * @param {Player} player
   */
  playerSelectionChange(player: Player) {
    this.playerAddForm.controls['assetId'].setValue(player.assetId);
    this.playerAddForm.controls['rating'].setValue(player.rating);
    this.playerAddForm.controls['firstName'].setValue(player.firstName);
    this.playerAddForm.controls['lastName'].setValue(player.lastName);
    this.playerAddForm.controls['commonName'].setValue(player.commonName);
    this.playerAddForm.controls['position'].setValue(player.position);
    this.playerAddForm.controls['nation'].setValue(player.nation);
    this.playerAddForm.controls['club'].setValue(player.club);
    this.playerAddForm.controls['league'].setValue(player.league);

    this.nationInput.setValue(player.nation);
  }

  /**
   * Handle position input change
   */
  positionInputChange(event: any) {
    this.playerAddForm.controls['position'].setValue(event.value);
  }

  /**
   * Handle nation input change
   */
  nationInputChange(event: any) {
    this.playerAddForm.controls['nation'].setValue(event.value);
  }

  /**
   * Handle club input change
   */
  clubInputChange(event: any) {
    this.playerAddForm.controls['club'].setValue(event.value);
  }

  /**
   * Handle league input change
   */
  leagueInputChange(event: any) {
    this.playerAddForm.controls['league'].setValue(event.value);
  }
}
