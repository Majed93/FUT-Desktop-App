import {Component, isDevMode, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../services/auth.service';
import {GlobalService} from '../globals/global.service';
import {WebSocketService} from '../services/websocket.service';

@Component({
  moduleId: module.id,
  selector: 'app-login',
  templateUrl: 'login.component.html',
  styleUrls: ['login.component.scss']
})
export class LoginComponent implements OnInit {
  authorized: boolean;
  email: string;
  key: string;
  errorAuth: string;
  loading = true;
  serviceUndefinedMsg = 'Unable to reach service. Please try again later.';

  /**
   * Constructor.
   *
   * @param authService The {@link AuthService}
   * @param route The {@link ActivatedRoute}
   * @param router The {@link Router}
   * @param globals The {@link GlobalService}
   * @param websocketService The {@link WebSocketService}
   */
  constructor(private authService: AuthService, private route: ActivatedRoute,
              private router: Router, private globals: GlobalService,
              private websocketService: WebSocketService) {
    // NOOP
  }

  ngOnInit(): void {
    // Need to check if auth file exists, if yes auto login.
    this.loading = true;

    if (isDevMode()) {
      this.globals.service_started = true;
    } else {
      const {ipcRenderer} = electron;
      ipcRenderer.once('servicePort', (event, arg) => {
          // Set port
          if (arg === null) {
            console.error('Error starting app.');
          } else if (arg < 0) {
            // Loop after a timer or something.
            console.log('still waiting..');
          } else {
            this.globals.service_started = true;
            this.globals.service_port = arg;
            console.log('port: ' + arg);
          }
        }
      );
    }

    const checkLimit = 100;
    const delay = 5000;

    this.checkService(0, delay, checkLimit);
  }

  /**
   * Check if service is up and running.
   *
   * @param index
   * @param delay
   * @param checkLimit
   */
  checkService(index, delay, checkLimit): void {
    setTimeout(() => {
      console.log('Checking if service started. Attempt ' + index);
      if (this.globals.service_started) {
        this.authorize();
        index = checkLimit;
      }

      index++;
      if (index < checkLimit) {
        this.checkService(index, delay, checkLimit);
      }
    }, delay);
  }

  /**
   * Authorise the user to enter the app.
   */
  authorize(): void {
    this.authService.getPort().subscribe(resp => {
        this.setEndpoints(isDevMode() ? resp['port'] : this.globals.service_port);

        this.authService.getAuth().subscribe(authResp => {
          if (authResp) {
            this.email = authResp.email;
            this.key = authResp.key;
          }

          if (this.email && this.key) {
            this.login();
          }
          this.loading = false;
        }, error => {
          this.loading = false;
          console.error('Error on login component authorizing: ' + JSON.stringify(error));
        });

      },
      err => {
        console.error('Error setting endpoint:' + JSON.stringify(err));
        this.errorAuth = JSON.stringify(err);
        this.loading = false;
      });
  }

  /**
   * Login to the application.
   */
  login(): void {
    this.loading = true;
    this.authService.doAuth(this.email, this.key).then(
      () => {
        this.authorized = this.authService.isAuthorised();
        if (this.authorized) {
          this.globals.global_auth = this.authorized;
          this.globals.csrf_token = ''; // this.authService.token;
          this.authService.saveAuth(this.email, this.key);
          this.loading = false;
          this.websocketService.connect();
          this.router.navigate(['main/accounts', {auth: true}]);
        }
      }, error => {
        this.loading = false;
        // Parse error message
        // Check if it has 'Invalid credentials' message
        if (error.error.message) {
          this.errorAuth = error.error.message;
        } else {
          this.errorAuth = this.serviceUndefinedMsg;
        }
        console.error('Error on login component authorizing: ' + JSON.stringify(error));
      });
  }

  /**
   * Set endpoints.
   * @param port The port to set.
   */
  private setEndpoints(port: any) {
    this.globals.endpoint = this.globals.API_PATH + port;
    this.globals.wsEndpoint = this.globals.WS_PATH + port + this.globals.WS_PREFIX;
  }
}
