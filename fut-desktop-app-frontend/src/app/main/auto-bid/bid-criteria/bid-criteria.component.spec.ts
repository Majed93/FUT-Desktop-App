import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {BidCriteriaComponent} from './bid-criteria.component';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule} from '@angular/forms';

describe('BidCriteriaComponent', () => {
  let component: BidCriteriaComponent;
  let fixture: ComponentFixture<BidCriteriaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        BidCriteriaComponent
      ],
      imports: [
        BrowserModule,
        FormsModule
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BidCriteriaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
