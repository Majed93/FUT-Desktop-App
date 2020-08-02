import {Injectable, Input} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {GlobalService} from '../globals/global.service';
import {throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';

@Injectable()
export class StateService {

  @Input() stopEndpoint: string;
  @Input() pauseEndpoint: string;

  /**
   * Constructor.
   *
   * @param {HttpClient} http
   * @param {GlobalService} global
   */
  constructor(private http: HttpClient, private global: GlobalService) {
  }

  /**
   * Pause the search.
   */
  pause() {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this.http.get(this.pauseEndpoint,
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.handleError));
  }

  /**
   * Stop the search.
   */
  stop() {
    const headers = new HttpHeaders().set('X-XSRF-TOKEN', this.global.csrf_token);
    return this.http.get(this.stopEndpoint,
      {
        headers: headers, withCredentials: true
      }).pipe(catchError(this.handleError));
  }

  /**
   * Handle any errors.
   *
   * @param {Response} error
   * @returns {ErrorObservable}
   */
  private handleError(error: Response) {
    console.error('Error in an request' + error);
    return throwError(error || 'Server error');
  }
}
