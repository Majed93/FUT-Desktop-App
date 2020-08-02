import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TradePileComponent} from './trade-pile.component';

xdescribe('TradePileComponent', () => {
  let component: TradePileComponent;
  let fixture: ComponentFixture<TradePileComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TradePileComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TradePileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
