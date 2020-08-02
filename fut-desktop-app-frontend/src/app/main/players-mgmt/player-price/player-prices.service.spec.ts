import {inject, TestBed} from '@angular/core/testing';

import {PlayerPricesService} from './player-prices.service';

xdescribe('PlayerPricesService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PlayerPricesService]
    });
  });

  it('should be created', inject([PlayerPricesService], (service: PlayerPricesService) => {
    expect(service).toBeTruthy();
  }));
});
