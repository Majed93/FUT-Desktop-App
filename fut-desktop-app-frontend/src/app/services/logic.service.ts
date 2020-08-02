import {Injectable} from '@angular/core';
import {GlobalService} from '../globals/global.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable()
export class LogicService {

  constructor(private _http: HttpClient, private global: GlobalService) {

  }

  /**
   * Login for the given account
   *
   * @param id The id of the account
   * @param appVersion The AppVersion - WebApp or CompanionApp
   */
  login(id: number, appVersion: string): Observable<any> {
    const headers: HttpHeaders = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);

    const login_endpoint = this.global.endpoint + this.global.LOGIN_PATH;
    return this._http.post(login_endpoint, { id: id, appVersion: appVersion}, {
      headers: headers, withCredentials: true
    });
  }
}
