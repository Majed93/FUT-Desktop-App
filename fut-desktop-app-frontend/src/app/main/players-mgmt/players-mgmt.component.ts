import {Component, Input, OnInit} from '@angular/core';
import {Player} from '../../domain/player';
import {PlayerListService} from './player-list/player-list.service';
import {GlobalService} from '../../globals/global.service';

@Component({
  moduleId: module.id,
  selector: 'app-players-mgmt',
  templateUrl: 'players-mgmt.component.html',
  styleUrls: ['players-mgmt.component.scss']
})
export class PlayersMgmtComponent implements OnInit {

  /* Array of files */
  fileName: string[];
  /* Selected JSON file. */
  @Input() selectedFile: string;
  /* Array of players in current list */
  playerList: Player[];
  /* Current tab to show */
  activeTab: string;
  loggedIn: boolean;

  constructor(private playerListService: PlayerListService, private globals: GlobalService) {
  }

  ngOnInit() {
    this.playerListService.getFileName().subscribe(resp => {
      this.fileName = resp;
      this.selectedFile = this.fileName[0];
      this.onChange();
    }, error => {
      console.error('Error getting files names: ' + JSON.stringify(error));
    });
    this.activeTab = 'player-list';
    this.loggedIn = this.globals.loggedIn;
  }

  /**
   * On player list change
   */
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

  /**
   * Set the new tab
   *
   * @param {string} newTab new tab to set
   */
  onTabChange(newTab: string): void {
    this.activeTab = newTab;
  }

}
