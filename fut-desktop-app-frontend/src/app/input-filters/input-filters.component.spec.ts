import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {InputFiltersComponent} from './input-filters.component';

xdescribe('InputFiltersComponent', () => {
  let component: InputFiltersComponent;
  let fixture: ComponentFixture<InputFiltersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InputFiltersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InputFiltersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
