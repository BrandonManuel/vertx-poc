package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.PreAuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.serviceproxy.ServiceBinder;

public class PostgresVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresVerticle.class);

    private SQLClient sqlClient;

    @Override
    public void start(Promise<Void> startPromise) {
        LOGGER.debug("Start verticle");
        try {
            sqlClient = PostgreSQLClient.createShared(vertx, new JsonObject().put("host", "localhost")
                    .put("database", "postgres").put("username", "postgres").put("password", "cor3services!"));
            PreAuthService.create(sqlClient, ready -> {
                if (ready.failed()) {
                    LOGGER.error("Failed to start postgres verticle", ready.cause());
                    startPromise.fail(ready.cause());
                } else {
                    LOGGER.debug("PreAuthService created");
                    ServiceBinder binder = new ServiceBinder(vertx);
                    PreAuthService authService = ready.result();
                    LOGGER.debug("PreAuthService bind start! with {}", authService);
                    binder.setAddress("gcpreauth").register(PreAuthService.class, authService);
                    LOGGER.debug("PreAuthService bound. Started verticle");
                    startPromise.complete();
                }
            });
        } catch (Exception e) {
            LOGGER.error("Failed to register service", e);
        }
    }
}
