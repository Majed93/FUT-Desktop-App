# FUT Desktop App
> NOTE: This app was created for FUT 19 and local usage and will no longer work with real EA servers. 
---
### Introduction

This project contains the following components:
* fut-desktop-app-frontend
    * Developed using Electron & Angular 7.x
    * UI for this app
* fut-desktop-app-backend
    * Comprises of several components (All Java & Spring) to serve data for the UI
    * Thanks to [trydis](https://github.com/trydis/FIFA-Ultimate-Team-Toolkit), used as reference for these components: fut-domain + fut-io 
    * fut-domain
    * fut-io (Spring boot app)
    * fut-service (Spring boot app)
    * fut-simulator (Spring boot app)
    * player-lists (Number of .json files used to load players)
* fut-integration-tests
    * Automation test suite

This repo will **NOT** be maintained but questions can be asked (general/coding help). Pull requests welcome.


Order to run in (if not running .exe):
* fut-service
    * **On first run the following endpoint must be called http://localhost:7002/setup** 
* fut-simulator
* fut-io
* frontend


Video of automation tests running:

[![https://youtu.be/kBLJ9e79LUs](https://img.youtube.com/vi/kBLJ9e79LUs/0.jpg)](https://youtu.be/kBLJ9e79LUs)
