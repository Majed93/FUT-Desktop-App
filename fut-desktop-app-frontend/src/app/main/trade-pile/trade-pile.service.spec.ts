import {inject, TestBed} from '@angular/core/testing';

import {TradePileService} from './trade-pile.service';

xdescribe('TradePileService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TradePileService]
    });
  });

  it('should be created', inject([TradePileService], (service: TradePileService) => {
    expect(service).toBeTruthy();
  }));
});
