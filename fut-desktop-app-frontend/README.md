# FUT Desktop App Frontend
---
### Introduction
Electron app developed using Angular 7.x and Bootstrap 4

Spring boot app(s) must be running to login and authenticate 

Running:

Run the following commands in order:

* `build`
* `start-ng` < This is live reload mode
* `electron`

Create electron exe:
* Copy fut-io & fut-service jar files from backend projects, to dist folder
* Run `release:ci`
* .exe will be in app-builds.
    * Since jar files are packaged up, the spring boot apps will run along with the .exe


Can be used as an example for:
* Websockets (client using stomp library), used for live data refresh
* Basic side bar animation
* Bootstrap + JQuery in Angular (not recommended using JQuery however)
* Custom Bootstrap theme
* Electron and Angular integration
* Typescript + Sonar metrics
* Use of Angular Guard(Login)
