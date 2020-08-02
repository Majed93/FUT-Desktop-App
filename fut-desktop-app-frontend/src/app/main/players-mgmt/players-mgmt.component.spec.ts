import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PlayersMgmtComponent} from './players-mgmt.component';

xdescribe('PlayersMgmtComponent', () => {
  let component: PlayersMgmtComponent;
  let fixture: ComponentFixture<PlayersMgmtComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PlayersMgmtComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayersMgmtComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
