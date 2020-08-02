import {Component, Input} from '@angular/core';
import {sideBarAnimation} from '../animations/animations';
import {Account} from '../domain/account';

@Component({
  moduleId: module.id,
  selector: 'app-sidebar',
  templateUrl: 'sidebar.component.html',
  styleUrls: ['sidebar.component.scss'],
  animations: [sideBarAnimation()]
})
export class SidebarComponent {

  sidebar_toggle = 'right';
  width: number;
  @Input() showMenu: string;
  @Input() playersLoaded: boolean;
  @Input() account: Account;

  constructor() {
  }

  toggleSidebar() {
    this.sidebar_toggle = this.sidebar_toggle === 'right' ? 'left' : 'right';
  }

  /**
   * Account has changed
   *
   * @param {Account} newAccount
   */
  accountChange(newAccount: Account): void {
    this.account = newAccount;
  }
}
