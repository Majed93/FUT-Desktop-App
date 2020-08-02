import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {StatusModalComponent} from './status-modal.component';

xdescribe('StatusModalComponent', () => {
  let component: StatusModalComponent;
  let fixture: ComponentFixture<StatusModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StatusModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StatusModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
