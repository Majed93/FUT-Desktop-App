import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {AuthService} from '../services/auth.service';
import {GlobalService} from '../globals/global.service';


@Injectable()
export class LoginGuard implements CanActivate {
  constructor(private authService: AuthService, private _router: Router, private globals: GlobalService) {
  }

  canActivate(): boolean {
    if (this.authService.isAuthorised()) {
      return true;
    } else {
      this._router.navigate(['/login']);
      return false;
    }
  }
}
