# Chatty

This is a simple chat implemented with [Server-Sent Events](https://www.baeldung.com/spring-server-sent-events) with Spring Boot

## Overview
- Messages are sent by the client over standard HTTP
- Messages are received by the server over a SSE stream
- There is a simple chat broker by chat room
- The server sends periodically keep-alive messages to the client in order to keep or close the connection


## How to run (for now)

- Execute the spring boot app api on http://localhost:8080 
- cd into `/frontend` folder and run the Angular frontend with `ng serve` on http://localhost:4200/

## Improvements

- Obviously a decent docker-compose.yml
- Chat room removal from broker if there's no connection after a while
