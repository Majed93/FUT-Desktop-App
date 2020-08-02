import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {AlertInterface} from './alert.interface';
import {AlertService} from './alert.service';
import {Alert} from './alert';

@Component({
  moduleId: module.id,
  selector: 'app-alert',
  templateUrl: 'alert.component.html',
  styleUrls: ['alert.component.scss']
})
export class AlertComponent implements OnInit {
  classes: string;
  text: string;
  messages: AlertInterface[] = [];
  timeOut = 4000;

  constructor(private alertService: AlertService, private _cdRef: ChangeDetectorRef) {
    this.alertService.show = this.show.bind(this);
  }

  ngOnInit(): void {
  }

  show(text?: string, options = {}): void {
    const defaults = {
      timeout: this.timeOut,
      cssClass: ''
    };

    for (const attrName in options) {
      if (attrName) {
        (defaults as any)[attrName] = (options as any)[attrName];
      }
    }

    const message = new Alert(text, defaults.cssClass);
    this.messages.push(message);
    this._cdRef.detectChanges();

    window.setTimeout(() => {
      this.remove(message);
      this._cdRef.detectChanges();
    }, defaults.timeout);
  }

  private remove(message: AlertInterface) {
    this.messages = this.messages.filter((msg) => msg.id !== message.id);
  }
}
