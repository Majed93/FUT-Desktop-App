// Karma configuration file, see link for more information
// https://karma-runner.github.io/0.13/config/configuration-file.html

module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage-istanbul-reporter'),
      require('@angular-devkit/build-angular/plugins/karma'),
      require('karma-scss-preprocessor')
    ],
    client: {
      clearContext: false // leave Jasmine Spec Runner output visible in browser
    },
    coverageIstanbulReporter: {
      dir: require('path').join(__dirname, 'coverage'), reports: ['html', 'lcovonly'],
      fixWebpackSourcePaths: true
    },
    
    reporters: ['progress', 'kjhtml'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['Chrome'],
    singleRun: false,
    files: [
      {pattern: 'node_modules/jquery/dist/jquery.slim.min.js', watched: true, included: true, served: true},
      {pattern: 'node_modules/popper.js/dist/umd/popper.js', watched: true, included: true, served: true},
      {pattern: 'node_modules/bootstrap/dist/js/bootstrap.min.js', watched: true, included: true, served: true},
      {pattern: 'src/assets/all.js', watched: true, included: true, served: true},
      {pattern: 'node_modules/bootstrap/scss/bootstrap.scss', watched: true, included: true, served: true},
      {pattern: 'src/styles.scss', watched: true, included: true, served: true}
    ],
    preprocessors: {
      'node_modules/bootstrap/scss/bootstrap.scss': ['scss'],
      'src/styles.scss': ['scss']
    }
  });
};
