import {Injectable} from '@angular/core';

@Injectable()
export class AlertService {

  public show(text?: string, options?: Object): void {
    console.debug('Alert displayed: ', text, options);
  }
}
