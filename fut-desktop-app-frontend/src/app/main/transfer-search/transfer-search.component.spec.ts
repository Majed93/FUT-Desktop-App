import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TransferSearchComponent} from './transfer-search.component';

xdescribe('TransferSearchComponent', () => {
  let component: TransferSearchComponent;
  let fixture: ComponentFixture<TransferSearchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TransferSearchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TransferSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
