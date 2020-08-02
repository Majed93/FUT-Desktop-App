import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {AlertComponent} from './alert.component';
import {AlertService} from './alert.service';
import {By} from '@angular/platform-browser';

describe('AlertComponent', () => {
  let component: AlertComponent;
  let fixture: ComponentFixture<AlertComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AlertComponent],
      providers: [AlertService]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    const alertElement = fixture.debugElement.query(By.css('alert'));

    expect(component.messages.length).toBe(0);
    expect(alertElement).toBeFalsy();
    expect(component).toBeTruthy();
  });

  it('should display an alert and remove it after the timeout.', fakeAsync(() => {
    const testMessage = 'Test';
    const type = 'success';
    component.show(testMessage, {cssClass: type});
    fixture.detectChanges();

    let alertElement = fixture.debugElement.query(By.css('.alert'));
    expect(component.messages.length).toBe(1);
    expect(alertElement.nativeElement).toBeTruthy();
    expect(alertElement.nativeElement.textContent).toContain(testMessage);
    expect(alertElement.properties.className).toContain('alert-' + type);
    expect(component).toBeTruthy();
    tick(component.timeOut);
    fixture.detectChanges();

    alertElement = fixture.debugElement.query(By.css('.alert'));
    expect(alertElement).toBeFalsy();
  }));
});
