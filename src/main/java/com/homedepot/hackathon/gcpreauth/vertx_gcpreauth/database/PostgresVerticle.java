package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.serviceproxy.ServiceBinder;

public class PostgresVerticle extends AbstractVerticle {

    private SQLClient sqlClient;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        sqlClient = PostgreSQLClient.createShared(vertx, new JsonObject().put("host", "ld09245.homedepot.com")
                .put("database", "postgres").put("username", "postgres").put("password", "cor3services!"));

    }

//    		CheckAuthService.create(sqlClient, ready -> {
//        if (ready.failed()) {
//            startFuture.fail(ready.cause());
//        } else {
//            ServiceBinder binder = new ServiceBinder(vertx).setAddress("chkauthaddress");
//            binder.registerLocal(CheckAuthService.class, ready.result());
//            if(LOGGER.isDebugEnabled()) {
//                LOGGER.debug("Starting HTTP Verticle");
//            }
//            startFuture.complete();
//        }
//    });

}