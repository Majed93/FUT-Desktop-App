import {GlobalService} from './global.service';
import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {HttpClientXsrfModule} from '@angular/common/http';
import {CookieModule} from 'ngx-cookie';
import {Account} from '../domain/account';
import {Player} from '../domain/player';
import {of, throwError} from 'rxjs';

describe('GlobalService', () => {
  let service: GlobalService;
  let httpMock: HttpTestingController;
  let allPlayerJsonUrl;
  let allCLubsJsonUrl;
  let allNationsJsonUrl;
  let allLeaguesJsonUrl;
  let allPositionJsonUrl;

  /** JSON Test files */
  const allPlayersJson: any = require('../../test-assets/test.players.json');
  const allClubsJson: any = require('../../test-assets/test.clubs.json');
  const allNationsJson: any = require('../../test-assets/test.nations.json');
  const allLeaguesJson: any = require('../../test-assets/test.leagues.json');
  const allPositionsJson: any = require('../../test-assets/test.positions.json');
  /**
   * Run before each test
   */
  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        HttpClientXsrfModule,
        CookieModule.forChild()],
      providers: [
        GlobalService]
    })
      .compileComponents();
  });

  /**
   * Run before each test
   */
  beforeEach(() => {
    service = TestBed.get(GlobalService);
    httpMock = TestBed.get(HttpTestingController);
    allPlayerJsonUrl = service.endpoint + service.PLAYERLIST_PATH + service.ALLPLAYERS_PATH;
    allCLubsJsonUrl = service.endpoint + service.PLAYERLIST_PATH + service.TEAMS_PATH;
    allNationsJsonUrl = service.endpoint + service.PLAYERLIST_PATH + service.NATIONS_PATH;
    allLeaguesJsonUrl = service.endpoint + service.PLAYERLIST_PATH + service.LEAGUES_PATH;
    allPositionJsonUrl = service.endpoint + service.PLAYERLIST_PATH + service.POSITIONS_PATH;
  });

  /**
   * RUn after each test
   */
  afterEach(() => {
    httpMock.verify();
  });

  /**
   * Verify service is created.
   */
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  /**
   * Verify resources retrieved successfully
   */
  it('should retrieve all resources successfully', () => {
    const spyOnGetAllTeams = spyOn(service, 'getAllTeams').and.returnValue(of(allClubsJson));
    const spyOnGetAllNations = spyOn(service, 'getAllNations').and.returnValue(of(allNationsJson));
    const spyOnGetAllLeagues = spyOn(service, 'getAllLeagues').and.returnValue(of(allLeaguesJson));
    const spyOnGetAllPositions = spyOn(service, 'getPositions').and.returnValue(of(allPositionsJson));

    service.getPlayerResourceData();
    expect(service.isPlayerResourcesLoaded).toBe(true);
    expect(spyOnGetAllTeams).toHaveBeenCalled();
    expect(spyOnGetAllNations).toHaveBeenCalled();
    expect(spyOnGetAllLeagues).toHaveBeenCalled();
    expect(spyOnGetAllPositions).toHaveBeenCalled();
  });

  /**
   * Verify errors on retrieving resources
   */
  it('should retrieve all resources unsuccessfully', () => {
    const spyOnGetAllTeams = spyOn(service, 'getAllTeams').and.returnValue(throwError({status: 500}));
    const spyOnGetAllNations = spyOn(service, 'getAllNations').and.returnValue(throwError({status: 500}));
    const spyOnGetAllLeagues = spyOn(service, 'getAllLeagues').and.returnValue(throwError({status: 500}));
    const spyOnGetAllPositions = spyOn(service, 'getPositions').and.returnValue(throwError({status: 500}));

    service.getPlayerResourceData();
    expect(service.isPlayerResourcesLoaded).toBe(false);
    expect(spyOnGetAllTeams).toHaveBeenCalled();
    expect(spyOnGetAllNations).toHaveBeenCalled();
    expect(spyOnGetAllLeagues).toHaveBeenCalled();
    expect(spyOnGetAllPositions).toHaveBeenCalled();
  });

  /**
   * Test all players JSON can be retrieved successfully
   */
  it('should get all players JSON', () => {
    service.getAllPlayersJson().subscribe(res => {
      expect(res).toBe(allPlayersJson);
    });

    const req = httpMock.expectOne(allPlayerJsonUrl);
    expect(req.request.method).toBe('GET');
    req.flush(allPlayersJson);
  });

  /**
   * Test all players JSON can be retrieved successfully
   * Verify test called not called because it has been loaded before.
   */
  it('should get all players JSON after being loaded in previously.', () => {
    service.isPlayersLoaded = true;
    service.allPlayers = allPlayersJson;
    service.getAllPlayersJson().subscribe(res => {
      expect(res).toBe(allPlayersJson);
    });

    httpMock.expectNone(allPlayerJsonUrl);
  });


  /**
   * Tests when an error is thrown while trying to retrieve all players JSON
   */
  it('should error when getting all players JSON', () => {
    service.getAllPlayersJson().subscribe(() => {
    }, err => {
      expect(err).toBeDefined();
    });

    httpMock.expectOne(allPlayerJsonUrl).error(new ErrorEvent('Bad Request'));
  });

  /**
   * Test all clubs JSON can be retrieved successfully
   */
  it('should get all clubs JSON', () => {
    service.getAllTeams().subscribe(res => {
      expect(res).toBe(allClubsJson);
    });

    const req = httpMock.expectOne(allCLubsJsonUrl);
    expect(req.request.method).toBe('GET');
    req.flush(allClubsJson);
  });

  /**
   * Test all clubs JSON can be retrieved successfully
   * Verify test called not called because it has been loaded before.
   */
  it('should get all clubs JSON after being loaded in previously.', () => {
    service.isPlayerResourcesLoaded = true;
    service.allTeams = allClubsJson;
    service.getAllTeams().subscribe(res => {
      expect(res).toBe(allClubsJson);
    });

    httpMock.expectNone(allCLubsJsonUrl);
  });

  /**
   * Test all nations JSON can be retrieved successfully
   */
  it('should get all nations JSON', () => {
    service.getAllNations().subscribe(res => {
      expect(res).toBe(allNationsJson);
    });

    const req = httpMock.expectOne(allNationsJsonUrl);
    expect(req.request.method).toBe('GET');
    req.flush(allNationsJson);
  });

  /**
   * Test all nations JSON can be retrieved successfully
   * Verify test called not called because it has been loaded before.
   */
  it('should get all nations JSON after being loaded in previously.', () => {
    service.isPlayerResourcesLoaded = true;
    service.allNations = allNationsJson;
    service.getAllNations().subscribe(res => {
      expect(res).toBe(allNationsJson);
    });

    httpMock.expectNone(allNationsJsonUrl);
  });

  /**
   * Tests when an error is thrown while trying to retrieve all nations JSON
   */
  it('should error when getting all nations JSON', () => {
    service.getAllNations().subscribe(() => {
    }, err => {
      expect(err).toBeDefined();
    });

    httpMock.expectOne(allNationsJsonUrl).error(new ErrorEvent('Bad Request'));
  });

  /**
   * Test all leagues JSON can be retrieved successfully
   */
  it('should get all leagues JSON', () => {
    service.getAllLeagues().subscribe(res => {
      expect(res).toBe(allLeaguesJson);
    });

    const req = httpMock.expectOne(allLeaguesJsonUrl);
    expect(req.request.method).toBe('GET');
    req.flush(allLeaguesJson);
  });

  /**
   * Test all nations JSON can be retrieved successfully
   * Verify test called not called because it has been loaded before.
   */
  it('should get all leagues JSON after being loaded in previously.', () => {
    service.isPlayerResourcesLoaded = true;
    service.allLeagues = allLeaguesJson;
    service.getAllLeagues().subscribe(res => {
      expect(res).toBe(allLeaguesJson);
    });

    httpMock.expectNone(allLeaguesJsonUrl);
  });

  /**
   * Tests when an error is thrown while trying to retrieve all nations JSON
   */
  it('should error when getting all leagues JSON', () => {
    service.getAllLeagues().subscribe(() => {
    }, err => {
      expect(err).toBeDefined();
    });

    httpMock.expectOne(allLeaguesJsonUrl).error(new ErrorEvent('Bad Request'));
  });

  /**
   * Test all leagues JSON can be retrieved successfully
   */
  it('should get all positions JSON', () => {
    service.getPositions().subscribe(res => {
      expect(res).toBe(allPositionsJson);
    });

    const req = httpMock.expectOne(allPositionsJson);
    expect(req.request.method).toBe('GET');
    req.flush(allPositionsJson);
  });

  /**
   * Test all nations JSON can be retrieved successfully
   * Verify test called not called because it has been loaded before.
   */
  it('should get all positions JSON after being loaded in previously.', () => {
    service.isPlayerResourcesLoaded = true;
    service.allPositions = allPositionsJson;
    service.getPositions().subscribe(res => {
      expect(res).toBe(allPositionsJson);
    });

    httpMock.expectNone(allPositionsJson);
  });

  /**
   * Tests when an error is thrown while trying to retrieve all nations JSON
   */
  it('should error when getting all positions JSON', () => {
    service.getPositions().subscribe(() => {
    }, err => {
      expect(err).toBeDefined();
    });

    httpMock.expectOne(allPositionsJson).error(new ErrorEvent('Bad Request'));
  });

  /**
   * Event emitted should react to a new account being added.
   */
  it('should react on a new account being added.', () => {
    const account: Account = new Account();
    const accounts: Account[] = [];
    accounts.push(account);

    const spyNewAccount = spyOn(service.onNewAccount, 'emit');

    service.newAccount(accounts);

    expect(spyNewAccount).toHaveBeenCalledWith(accounts);
  });

  /**
   * Should human readable platform string.
   */
  it('should return the platform in a user readable format', () => {
    let platformKey = 'Pc';
    let result = service.getPlatform(platformKey);
    expect(result).toBe('PC');

    platformKey = 'Ps3';
    result = service.getPlatform(platformKey);
    expect(result).toBe('PS3');

    platformKey = 'Ps4';
    result = service.getPlatform(platformKey);
    expect(result).toBe('PS4');

    platformKey = 'Xbox360';
    result = service.getPlatform(platformKey);
    expect(result).toBe('Xbox 360');

    platformKey = 'XboxOne';
    result = service.getPlatform(platformKey);
    expect(result).toBe('Xbox One');
  });

  /**
   * Verify the number is verified up to be an acceptable bid amount
   */
  it('should round the number to the nearest acceptable bid amount', () => {
    let value = 0;
    let result = service.roundToNearest(value);
    expect(result).toBe(0);

    value = 123;
    result = service.roundToNearest(value);
    expect(result).toBe(150);

    value = 840;
    result = service.roundToNearest(value);
    expect(result).toBe(850);

    value = 2340;
    result = service.roundToNearest(value);
    expect(result).toBe(2400);

    value = 12450;
    result = service.roundToNearest(value);
    expect(result).toBe(12500);

    value = 46875;
    result = service.roundToNearest(value);
    expect(result).toBe(47000);

    value = 86400;
    result = service.roundToNearest(value);
    expect(result).toBe(86500);

    value = 101100;
    result = service.roundToNearest(value);
    expect(result).toBe(102000);

    value = 150;
    result = service.roundToNearest(value);
    expect(result).toBe(150);

    value = 950;
    result = service.roundToNearest(value);
    expect(result).toBe(950);

    value = 2200;
    result = service.roundToNearest(value);
    expect(result).toBe(2200);

    value = 16750;
    result = service.roundToNearest(value);
    expect(result).toBe(16750);

    value = 47750;
    result = service.roundToNearest(value);
    expect(result).toBe(47750);

    value = 79000;
    result = service.roundToNearest(value);
    expect(result).toBe(79000);

    value = 151000;
    result = service.roundToNearest(value);
    expect(result).toBe(151000);
  });

  /**
   * Verify the correct step value is returned
   */
  it('should return correct step value', () => {
    let value = 0;
    let result = service.stepValue(value);
    expect(result).toBe(0);

    value = 100;
    result = service.stepValue(value);
    expect(result).toBe(50);

    value = 1100;
    result = service.stepValue(value);
    expect(result).toBe(100);

    value = 21000;
    result = service.stepValue(value);
    expect(result).toBe(250);

    value = 60000;
    result = service.stepValue(value);
    expect(result).toBe(500);

    value = 10000000;
    result = service.stepValue(value);
    expect(result).toBe(1000);
  });

  /**
   * Verify the correct bid amount is returned
   */
  it('should return the next bid amount', () => {
    let value = 0;
    let result = service.nextBidAmount(value);
    expect(result).toBe(0);

    value = 200;
    result = service.nextBidAmount(value);
    expect(result).toBe(250);

    value = 2000;
    result = service.nextBidAmount(value);
    expect(result).toBe(2100);

    value = 12250;
    result = service.nextBidAmount(value);
    expect(result).toBe(12500);

    value = 55000;
    result = service.nextBidAmount(value);
    expect(result).toBe(55500);

    value = 200000;
    result = service.nextBidAmount(value);
    expect(result).toBe(201000);
  });

  /**
   * Get the player name
   */
  it('should get the player name', () => {
    service.allPlayers = allPlayersJson;

    const player: Player = service.allPlayers[1];

    expect(player).toBeDefined();

    const result = service.getPlayerName(player);
    expect(result).toContain(player.firstName);
    expect(result).toContain(player.lastName);
  });

  /**
   * Get the player name with common name
   */
  it('should get the player name who has a common name', () => {
    service.allPlayers = allPlayersJson;

    const player: Player = service.allPlayers[0];

    expect(player).toBeDefined();

    const result = service.getPlayerName(player);
    expect(result).toContain(player.firstName);
    expect(result).toContain(player.lastName);
    expect(result).toContain(player.commonName);
    expect(result).toContain('(');
    expect(result).toContain(')');
  });

  /**
   * Verify accents are removed from string
   */
  it('should remove accents from a string', () => {
    const accentString = 'Tést';
    const result = service.removeAccents(accentString);

    expect(result).not.toBe(accentString);
    expect(result).toBe('Test');
  });
});
