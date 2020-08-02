import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PlayerPriceComponent} from './player-price.component';

xdescribe('PlayerPriceComponent', () => {
  let component: PlayerPriceComponent;
  let fixture: ComponentFixture<PlayerPriceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PlayerPriceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayerPriceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
