'use strict';

import {app, BrowserWindow, screen} from 'electron';

/**
 * Imports
 */
require('hazardous');

const path = require('path');
// *************************************************
const spawn_service = require('child_process').spawn;
const spawn_core = require('child_process').spawn;
// *************************************************
const fp = require('find-free-port');
const chalk = require('chalk');
const fs = require('fs');
let win, serve, isDevMode;
const args = process.argv.slice(1);
serve = args.some(val => val === '--serve'); // prod vs dev
isDevMode = args.some(val => val === '--dev');
const isIntTestMode = args.some(val => val === '--int_test');
const isCIServer = args.some(val => val === '--ci_server');
if (serve) {
  require('electron-reload')(__dirname, {});
}

// Run fut-service first.


// check if not NG SERVE or whatever TODO:
const jarDir = path.join(__dirname.replace('app.asar', 'app.asar.unpacked'));

/**
 *
 * @param data
 * @param fileNo 0 = normal, 1 = service, 2 = io
 */
function addToFile(data, fileNo) {
  let fileName = +'/';

  if (!serve) {
    if (fileNo === 1) {
      fileName = path.join(process.env.PORTABLE_EXECUTABLE_DIR, 'service.json');
    } else if (fileNo === 2) {
      fileName = path.join(process.env.PORTABLE_EXECUTABLE_DIR, 'io.json');
    } else {
      fileName = path.join(process.env.PORTABLE_EXECUTABLE_DIR, 'main.json');
    }

    data = new Date() + ' | ' + data + '\n';
    fs.appendFile(fileName, data, function (err) {
      if (err) {
        return console.log(err);
      }
    });
  }
}

let service;
let core_service;
// TODO:
const SERVICE_NAME = 'fut-service-1.0.0-exec.jar';
const IO_NAME = 'fut-io-1.0.0-exec.jar';

declare const jquery: any;
declare const $: any;

const INT_TEST_PORT = 'int_test_port';
const INT_TEST_LOCATION = 'int_test_location';

let intTestPort;
let intTestPath;


/** Messages to check when apps have started */
// TODO: ******** REMOVE WHEN NOT USING SIM *******************
// const startedFutIOApplicationIn = 'Started FutSimulatorApplication in';
const startedFutIOApplicationIn = 'Started FutIOApplication in';
const startedFutServiceApplicationIn = 'Started FutServiceApplication in';

if (isIntTestMode) {
  process.argv.slice(2).forEach(arg => {
    if (arg.includes(INT_TEST_PORT)) {
      const splitStr = arg.split('=');
      addToFile(INT_TEST_PORT + ' is ' + splitStr[1], 0);
      intTestPort = splitStr[1];
    }

    if (arg.includes(INT_TEST_LOCATION)) {
      const splitStr = arg.split('=');
      addToFile(INT_TEST_LOCATION + ' is ' + splitStr[1], 0);
      intTestPath = splitStr[1];
    }
  });

}

// Create the window
function createWindow() {
  const electronScreen = screen;
  const size = electronScreen.getPrimaryDisplay().workAreaSize;

  let screenZoomFactor = 1;
  // if less than 1080p
  if (!isCIServer && !isIntTestMode) {
    if (size.width < 1920 && size.height < 1080) {
      screenZoomFactor = 0.70;
    }
  }
  // Create the browser window.
  win = new BrowserWindow({
    x: 0,
    y: 0,
    width: size.width,
    height: size.height,
    webPreferences: {
      zoomFactor: screenZoomFactor
    }
  });

  // and load the index.html of the app.
  win.loadURL('file://' + __dirname + '/index.html');
  if (serve) {
    win.loadURL('http://localhost:4200');
  }

  // Open the DevTools.
  if (serve) {
    win.webContents.openDevTools();
  }

  win.webContents.on('did-finish-load', function () {
    if (isIntTestMode) {
      win.webContents.send('servicePort', intTestPort);
      win.webContents.removeAllListeners('servicePort');
    }
  });

  // Emitted when the window is closed.
  win.on('closed', () => {
    // Dereference the window object, usually you would store window
    // in an array if your app supports multi windows, this is the time
    // when you should delete the corresponding element.
    win = null;
  });

  win.maximize();

  if (isCIServer) {
    win.hide();
  }
}

let service_port = 1000;
let io_port = 2000; // TODO CHANGE LATER **************************************************************************

let servicePortCounter = 0;

// Find free port
function findPort(port) {
  return new Promise<number>(((resolve, reject) => {
    fp(port, '127.0.0.1', function (err, freePort) {
      if (port === freePort) {
        console.log(chalk.green('Port free: ' + port));
        addToFile('Port free: ' + port, 0);
        resolve(port);
      } else {
        console.error(chalk.red('Port in use! ' + port));
        addToFile('Port in use! ' + port, 0);

        // Try find another
        port++;
        if (servicePortCounter > 1000) {
          reject(err);
        }
        servicePortCounter++;
        resolve(findPort(port));
      }
    });
  }));
}

/**
 * Run service
 *
 * @returns {Promise<void>}
 */
function runService() {
  return new Promise<void>((resolve, reject) => {
    if (service_port === null || io_port === null) {
      console.error(chalk.red('Error starting service.'));
      addToFile('Error starting service.', 0);

      app.quit();
    }

    let dir = isDevMode ? 'M:/FUT Desktop App' : process.env.PORTABLE_EXECUTABLE_DIR;
    if (isIntTestMode) {
      dir = intTestPath;
    }
    addToFile('data dir - ' + dir, 0);

    // Calling runCore function
    runCore(dir).then(function () {
      console.log('Core service running');
      // , '--server.port=' + service_port,
      // '--working.dir=M:'
      // shell: true // Set to false if fails to start.
      // TODO: remove/add FIDDLER PARAMS
      //    service = spawn('cmd.exe', ['/c', 'java', '-jar', jarDir + 'assets/fut-service-1.0.0-exec.jar', '--server.port=4567'], {
      // SERVICE **************************************
      // TODO: add this param!! ***********
      const eP = '--fut.service.endpoint=http://156.212.100.5:' + io_port + '/io';
      // TODO: *************************************
      service = spawn_service('java', ['-jar', path.join(path.join(jarDir, 'assets'), SERVICE_NAME), '--server.port=' + service_port,
        '--working.dir=' + dir, eP], {
        detached: true
      }).on('error', (error) => {
        console.log(chalk.red('Unknown error: ' + error));
        addToFile('Unknown error: ' + error, 1);
        reject();
        app.quit();
      });

      //noinspection JSUnresolvedFunction
      service.stdout.setEncoding('utf8');
      service.stdout.on('data', function (data) {
        const str = data.toString();
        const lines = str.split(/(\r?\n)/g);
        console.log(lines.join(''));
        addToFile(lines.join(''), 1);
        if (lines.join('').indexOf(startedFutServiceApplicationIn) !== -1) {
          resolve();
        }
      });

      service.stderr.setEncoding('utf8');
      service.stderr.on('data', function (data) {
        const str = data.toString();
        const lines = str.split(/(\r?\n)/g);
        console.log(chalk.red(lines.join('')));
        addToFile(lines.join(''), 1);
        reject();
      });

      service.on('close', function (code) {
        console.log('process exit code ' + code);
        addToFile('process exit code ' + code, 1);
        // app.quit();
      });
    }, function (reason) { // Core service error
      console.error('Error running core service: ' + reason);
      addToFile('Error running core service: ' + reason, 2);
    });
  }).then(() => {
    console.log('Service running');
    addToFile('Service running', 0);
    win.webContents.send('servicePort', service_port);
    win.webContents.removeAllListeners('servicePort');
  }, function (reason) {
    console.error('Error running service: ' + reason);
    addToFile('Error running service: ' + reason, 0);
    win.webContents.send('servicePort', -1);
    win.webContents.removeAllListeners('servicePort');
  });
}


/**
 * CORE
 * @param dir
 * @returns {Promise<any>}
 */
function runCore(dir) {
  return new Promise(function (resolve, reject) {
    const ioPath = path.join(path.join(jarDir, 'assets'), IO_NAME);
    // addToFile(ioPath, 0);

    /* '-DproxySet=true',
       '-DproxyHost=127.0.0.1',
       '-DproxyPort=8888',
       '-Djavax.net.ssl.trustStore="C:/Program Files/Java/jdk1.8.0_172/jre/lib/security/cacerts"',
       '-Djavax.net.ssl.trustStorePassword=changeit',*/
    // CORE **************************************
    core_service = spawn_core('java', [
      // '-DproxySet=true',
      // '-DproxyHost=127.0.0.1',
      // '-DproxyPort=8888',
      // '-Djavax.net.ssl.trustStore="C:/Program Files/Java/jdk1.8.0_172/jre/lib/security/cacerts"',
      // '-Djavax.net.ssl.trustStorePassword=changeit',
      '-jar', ioPath, '--server.port=' + io_port,
      '--working.dir=' + dir], {
      detached: true
    }).on('error', (error) => {
      console.log(chalk.red('Unknown error: ' + error));
      addToFile(chalk.red('Unknown error: ' + error), 2);
      reject();
      app.quit();
    });

    //noinspection JSUnresolvedFunction
    core_service.stdout.setEncoding('utf8');
    core_service.stdout.on('data', function (data) {
      const str = data.toString();
      const lines = str.split(/(\r?\n)/g);
      console.log(lines.join(''));
      addToFile(lines.join(''), 2);

      if (lines.join('').indexOf(startedFutIOApplicationIn) !== -1) {
        resolve();
      }
    });

    core_service.stderr.setEncoding('utf8');
    core_service.stderr.on('data', function (data) {
      const str = data.toString();
      const lines = str.split(/(\r?\n)/g);
      console.log(chalk.red(lines.join('')));
      addToFile(lines.join(''), 2);
      reject();
    });

    core_service.on('close', function (code) {
      console.log('process exit code ' + code);
      addToFile('process exit code ' + code, 2);
      app.quit();
    });
  });
}

try {
  /**
   * Start services
   */
  app.on('browser-window-created', () => {
    addToFile(process.argv, 0);

    if (isIntTestMode === false && isDevMode === false) {
      findPort(service_port).then((result) => {
        service_port = result;
        console.log('srv: ' + service_port);
        findPort(io_port).then((result2) => {
          io_port = result2;
          console.log('io: ' + io_port);
          // Start running services
          runService().then(resp => {
            addToFile('success ' + resp, 0);
          }).catch((error) => {
            addToFile('error ' + error, 0);
            app.quit();
          });

        });
      }).catch(error => console.error('Error in chain: ' + error));
    } else {
      addToFile('Integration/Dev mode.', 0);
    }
  });
  // This method will be called when Electron has finished
  // initialization and is ready to create browser windows.
  // Some APIs can only be used after this event occurs.
  app.on('ready', () => {
    createWindow();
  });

  // Quit when all windows are closed.
  app.on('window-all-closed', () => {
    // On OS X it is common for applications and their menu bar
    // to stay active until the user quits explicitly with Cmd + Q
    if (process.platform !== 'darwin') {
      app.quit();
    }
  });

  app.on('activate', () => {
    // On OS X it's common to re-create a window in the app when the
    // dock icon is clicked and there are no other windows open.
    // TODO: **************************************************************
    if (win === null) {
      createWindow();
    }
  });

  app.on('browser-window-created', (e, window) => {
    window.setMenu(null);
  });

  app.on('before-quit', (e) => {
    if (!isIntTestMode && !isDevMode) {
      service.kill();
      core_service.kill();
    }
    addToFile('Error before quit.', 0);
    addToFile(e, 0);
  });
} catch (e) {
  addToFile(e, 0);
  // Catch Error
  // throw e;
}
