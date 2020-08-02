# FUT Desktop App Backend
---
### Introduction
> Java 8 required

Multi maven project consisting of:
* fut-domain
    * Request/Response objects & utils shared
* fut-io (Spring boot app)
    * App used to send requests to EA servers
* fut-service (Spring boot app)
    * Middleware between UI and EA requests
    * working.dir=M:/FUT Desktop App - change this property accordingly (where the electron app is running from)
* fut-simulator (Spring boot app)
    * Simulated EA endpoints
    * Trainable via REST calls (postman collection provided)


'player-lists' Is a collection of json files which loads in players to trade with. Scraped from futbin using [fut player scraper](https://github.com/Majed93/player-scraper) 
Can be edited manually or via the app.
> Minimal set of unit tests.

Can be used as an example for:
* Spring Boot
* Spring Security
* Spring Data JPA with SQL lite file storage
* Spring Web Sockets
