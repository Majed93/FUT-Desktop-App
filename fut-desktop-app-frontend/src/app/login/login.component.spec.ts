import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LoginComponent} from './login.component';
import {AuthService} from '../services/auth.service';
import {GlobalService} from '../globals/global.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {CookieModule} from 'ngx-cookie';
import {AutoBidComponent} from '../main/auto-bid/auto-bid.component';
import {MainComponent} from '../main/main.component';
import {PlayersMgmtComponent} from '../main/players-mgmt/players-mgmt.component';
import {AccountsComponent} from '../main/accounts/accounts.component';
import {TransferSearchComponent} from '../main/transfer-search/transfer-search.component';
import {TradePileComponent} from '../main/trade-pile/trade-pile.component';
import {WatchListComponent} from '../main/watch-list/watch-list.component';
import {AlertModule} from '../alert/module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppRoutingModule} from '../app-routing.module';
import {NgxPaginationModule} from 'ngx-pagination';
import {BrowserModule, By} from '@angular/platform-browser';
import {MultiselectDropdownModule} from 'angular-2-dropdown-multiselect';
import {APP_BASE_HREF} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {of, throwError} from 'rxjs';
import {UnassignedComponent} from '../main/unassigned/unassigned.component';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let globalService: GlobalService;
  let route: ActivatedRoute;
  let router: Router;

  let emailInput: HTMLElement;
  let keyInput: HTMLElement;

  const EMAIL_ID = 'email-input';
  const KEY_ID = 'key-input';

  const portJson: any = require('../../assets/port.json');

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AccountsComponent,
        LoginComponent,
        MainComponent,
        TradePileComponent,
        PlayersMgmtComponent,
        WatchListComponent,
        TransferSearchComponent,
        AutoBidComponent,
        UnassignedComponent
      ],
      imports: [
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        NgxPaginationModule,
        MultiselectDropdownModule,
        HttpClientModule,
        HttpClientXsrfModule.withOptions({
          cookieName: 'XSRF-TOKEN',
          headerName: 'XSRF-TOKEN'
        }),
        AlertModule,
        CookieModule.forChild()
      ],
      providers: [
        AuthService,
        GlobalService,
        {provide: APP_BASE_HREF, useValue: '/'}],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents();
  }));

  function updateComponentReferences() {
    emailInput = fixture.debugElement.query(By.css('#' + EMAIL_ID)).nativeElement;
    keyInput = fixture.debugElement.query(By.css('#' + KEY_ID)).nativeElement;
  }

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.get(AuthService);
    globalService = TestBed.get(GlobalService);
    route = TestBed.get(ActivatedRoute);
    router = TestBed.get(Router);

    updateComponentReferences();

    fixture.detectChanges();
  });

  /**
   * Verify component created.
   */
  it('should be created', () => {
    expect(emailInput).toBeTruthy();
    expect(keyInput).toBeTruthy();
    expect(component).toBeTruthy();
  });

  /**
   * Verify checking service is up.
   */
  it('should check the service once it has been started', () => {
    const authorizeSpy = spyOn(component, 'authorize').and.callFake(() => {
    });
    globalService.service_started = true;
    component.checkService(0, 0, 100);

    fixture.whenStable().then(() => {
      expect(authorizeSpy).toHaveBeenCalled();
    });
  });

  /**
   * Verify checking service is up.
   */
  it('should check the service after it has started.', () => {
    const authorizeSpy = spyOn(component, 'authorize').and.callFake(() => {
    });
    globalService.service_started = false;
    component.checkService(0, 1, 2);

    fixture.whenStable().then(() => {
      expect(authorizeSpy).toHaveBeenCalled();
    });
  });


  /**
   * Verify authorization of user.
   */
  it('should authorize the user.', () => {
    const testEmail = 'test@test.com';
    const testKey = '12345';

    const mockResponse = '{"email":"test@outlook.com","key":"ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"}';

    fixture.detectChanges();

    spyOn(authService, 'getPort').and.returnValue(of(portJson));
    const getAuth = spyOn(authService, 'getAuth').and.returnValue(of(mockResponse));
    const spyLogin = spyOn(component, 'login').and.callFake(() => {
    });
    component.authorize();

    fixture.whenStable().then(() => {
      expect(getAuth).toHaveBeenCalled();
      expect(component.email).toBe(testEmail);
      expect(component.key).toBe(testKey);
      expect(globalService.endpoint).toContain(portJson.port);
      expect(spyLogin).toHaveBeenCalled();
      expect(component.loading).toBeFalsy();
    });
  });

  /**
   * Verify error getting port when trying to authorize user.
   */
  it('should throw an error when trying to get the port.', () => {
    spyOn(authService, 'getPort').and.returnValue(throwError({status: 500}));
    const spyLogin = spyOn(component, 'login');
    component.authorize();

    fixture.whenStable().then(() => {
      expect(spyLogin).not.toHaveBeenCalled();
      expect(component.loading).toBeFalsy();
      expect(component.errorAuth).toBeTruthy();
    });
  });

  /**
   * Verify error authorizing user.
   */
  it('should throw an error unable to authorize user.', () => {
    spyOn(authService, 'getPort').and.returnValue(of(portJson));
    const getAuth = spyOn(authService, 'getAuth').and.returnValue(throwError({status: 403}));
    const spyLogin = spyOn(component, 'login').and.callFake(() => {
    });
    component.authorize();

    fixture.whenStable().then(() => {
      expect(getAuth).toHaveBeenCalled();
      expect(globalService.endpoint).toContain(portJson.port);
      expect(spyLogin).not.toHaveBeenCalled();
      expect(component.loading).toBeFalsy();
    });
  });

  /**
   * Verify login successfully.
   */
  it('should login successfully.', async () => {
    spyOn(authService, 'doAuth').and.callFake(() => {
      return new Promise<void>((resolve) => {
        resolve();
      });
    });
    spyOn(authService, 'isAuthorised').and.returnValue(true);
    const spyOnSaveAuth = spyOn(authService, 'saveAuth').and.callFake(() => {
    });
    const spyOnRouter = spyOn(router, 'navigate').and.callFake(() => {
    });
    component.login();

    fixture.whenStable().then(() => {
      expect(spyOnSaveAuth).toHaveBeenCalled();
      expect(globalService.allPlayers).toBeTruthy();
      expect(component.loading).toBeFalsy();
      expect(spyOnRouter).toHaveBeenCalled();
    });
  });


  /**
   * Verify login unsuccessfully.
   */
  it('should login unsuccessfully..', async () => {
    spyOn(authService, 'doAuth').and.callFake(() => {
      return new Promise<void>((resolve, reject) => {
        reject({
          error: {message: 'blah'},
          status: 500
        });
      });
    });
    spyOn(authService, 'isAuthorised').and.returnValue(false);
    const spyOnSaveAuth = spyOn(authService, 'saveAuth').and.callFake(() => {
    });
    const spyOnRouter = spyOn(router, 'navigate').and.callFake(() => {
    });

    component.login();

    fixture.whenStable().then(() => {
      expect(spyOnSaveAuth).not.toHaveBeenCalled();
      expect(globalService.allPlayers).toBeTruthy();
      expect(component.loading).toBe(false);
      expect(component.errorAuth).toBeTruthy();
      expect(spyOnRouter).not.toHaveBeenCalled();
    });
  });
});
