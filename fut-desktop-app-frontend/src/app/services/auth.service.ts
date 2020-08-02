import {Injectable} from '@angular/core';
import {Observable, throwError} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {CookieService} from 'ngx-cookie';
import {GlobalService} from '../globals/global.service';
import {catchError} from 'rxjs/operators';

@Injectable()
export class AuthService {

  private portFile = './assets/port.json';
  private saveAuthPath = this.globals.AUTH_PATH + '/saveAuth';
  private getAuthPath = this.globals.AUTH_PATH + '/getAuth';
  private _authorized;
  private _token;

  constructor(private _http: HttpClient, private cs: CookieService,
              private globals: GlobalService) {
  }

  isAuthorised(): boolean {
    return this._authorized;
  }

  get token() {
    return this._token;
  }

  public getPort(): Observable<string> {
    return this._http.get<string>(this.portFile)
      .pipe(catchError(this.handleError));
  }

  public doAuth(email, key) {
    return new Promise<void>((resolve, reject) => {
      this.authenticate(null, email, key).then(auth => {
        if (auth) {
          this._authorized = true;
          resolve();
        } else {
          reject();
        }
      }, error => {
        if (error.status === 403 || error.status === 401) {
          // set xsrf here and send again.
          const token = this.cs.get('XSRF-TOKEN');
          this.authenticate(token, email, key).then(data => {
              if (data) {
                this._token = token;
                this._authorized = true;
                resolve();
              } else {
                this._authorized = false;
                reject(data);
              }
            },
            err => {
              console.error('Error in second auth: ' + JSON.stringify(err));
              this._authorized = false;
              reject(err);
              return throwError(JSON.stringify(error) || 'Server error');
            }
          );

        } else if (error.status === 461) {
          console.error('Incorrect credentials: ' + JSON.stringify(error));
          this._authorized = false;
          reject(error);
          return throwError(JSON.stringify(error) || 'Server error');
        } else {
          console.error('Error authorizing: ' + JSON.stringify(error));
          this._authorized = false;
          reject(error);
          return throwError(JSON.stringify(error) || 'Server error');
        }
      })
    });
  }

  public authenticate(token, email, key) {
    // TODO: CHANGE THIS
    const body = email + '||' + key;
    let headers = new HttpHeaders().set('Content-Type', 'application/json');

    if (token != null) {
      headers = new HttpHeaders().set('X-XSRF-TOKEN', token);
    }

    return this._http.post(this.globals.endpoint + this.globals.AUTH_PATH, body, {
      headers: headers, withCredentials: true
    }).toPromise();

  }

  public saveAuth(email, key) {
    const data = {
      token: this.token,
      email: email,
      key: key
    };

    let headers = new HttpHeaders().set('Content-Type', 'application/json');

    if (this.token != null) {
      headers = new HttpHeaders().set('X-XSRF-TOKEN', this.token);
    }
    return this._http.post(this.globals.endpoint + this.saveAuthPath, data, {
      headers: headers, withCredentials: true
    }).toPromise();
  }

  public getAuth(): Observable<any> {
    let headers = new HttpHeaders().set('Content-Type', 'application/json');

    if (this.token != null) {
      headers = new HttpHeaders().set('X-XSRF-TOKEN', this.token);
    }

    return this._http.get(this.globals.endpoint + this.getAuthPath, {
      headers: headers, withCredentials: true
    });
  }

  private handleError(error: Response) {
    console.error(error);
    return throwError(error || 'Server error');
  }

}
