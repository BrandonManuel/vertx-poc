package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.PreAuthService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.serviceproxy.ServiceBinder;

public class PostgresVerticle extends AbstractVerticle {

    private SQLClient sqlClient;

    @Override
    public void start(Promise<Void> startPromise) {
        sqlClient = PostgreSQLClient.createShared(vertx, new JsonObject().put("host", "localhost")
                .put("database", "postgres").put("username", "postgres").put("password", "cor3services!"));
        PreAuthService.create(sqlClient, ready -> {
            if(ready.failed()) {
                startPromise.fail(ready.cause());
            } else {
                ServiceBinder serviceBinder = new ServiceBinder(vertx).setAddress("gcpreauth");
                serviceBinder.registerLocal(PreAuthService.class, ready.result());
                startPromise.complete();
            }
        });
    }
}
