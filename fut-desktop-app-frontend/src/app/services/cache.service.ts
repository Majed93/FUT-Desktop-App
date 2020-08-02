import {Injectable} from '@angular/core';
import {GlobalService} from '../globals/global.service';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {catchError} from 'rxjs/operators';

/**
 * Cache service.
 */
@Injectable()
export class CacheService {

  // Stop cache endpoint
  private stopCacheEndpoint = this.global.endpoint + this.global.CACHE_STOP;

  /**
   * Constructor
   */
  constructor(private httpClient: HttpClient, private global: GlobalService) {
    // NOOP
  }

  /**
   * Stop cache
   *
   * @returns {Observable<>}
   */
  stopCache(): Observable<any> {
    return this.httpClient.get(this.stopCacheEndpoint)
      .pipe(catchError(this.global.handleError));
  }
}
