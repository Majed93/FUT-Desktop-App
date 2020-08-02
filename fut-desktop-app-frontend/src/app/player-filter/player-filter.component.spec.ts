import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PlayerFilterComponent} from './player-filter.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {GlobalService} from '../globals/global.service';
import {HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {PlayerSearchPipe} from '../pipe/player-search.pipe';

describe('PlayerFilterComponent', () => {
  let component: PlayerFilterComponent;
  let fixture: ComponentFixture<PlayerFilterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PlayerFilterComponent, PlayerSearchPipe],
      imports: [
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule,
        HttpClientXsrfModule
      ],
      providers: [
        GlobalService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayerFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
