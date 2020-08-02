import {Component, EventEmitter, HostListener, Input, Output} from '@angular/core';
import {Player} from '../domain/player';
import {GlobalService} from '../globals/global.service';

@Component({
  moduleId: module.id,
  selector: 'app-player-filter',
  templateUrl: 'player-filter.component.html',
  styleUrls: ['player-filter.component.scss']
})
export class PlayerFilterComponent {
  ID_PREFIX = 'player-filer-';

  @Input() id;

  /** List to filter */
  @Input() allPlayers: Player[] = [];
  /** Output value to consumer */
  @Output() outputValue: EventEmitter<any> = new EventEmitter();
  /** Placeholder text for input*/
  @Input() placeholder;
  toCloseMenu = false;

  playerSelected: boolean;
  selectedPlayer: Player;
  /** Search text */
  playerSearchTerm: string;

  @HostListener('document:click', ['$event']) clickedOutside($event) {
    this.toCloseMenu = true;
  }

  constructor(private globals: GlobalService) {
    // NOOP
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
   * Clear inputs
   */
  reset() {
    this.playerSearchTerm = '';
    this.playerSelected = false;
    this.selectedPlayer = null;
  }

  /**
   * Select player from search box.
   *
   * @param {Player} player
   */
  selectPlayer(player: Player) {
    this.setSelectedPlayer(player);
    this.outputValue.emit(player);
  }

  /**
   * Helper to set selected items and reset other variables
   * @param player player to set
   */
  setSelectedPlayer(player: Player) {
    this.selectedPlayer = player;
    this.playerSearchTerm = this.getPlayerName(player);
    this.playerSelected = true;
  }

  /**
   * On search text changed event
   *
   * @param {Event} event
   */
  onSearchChange(event: Event) {
    this.toCloseMenu = false;
    if (this.playerSearchTerm === '') {
      this.toCloseMenu = true;
    }
  }
}
