package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;

import java.sql.Timestamp;
import java.util.UUID;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.impl.PreAuthServiceImpl;

@ProxyGen
public interface PreAuthService {

    @Fluent
    PreAuthService insertPreAuth(String gcardNbr,
                          Double authAmt,
                          String exprTs,
                          String authId,
                          String reqId,
                          String lastUpdatedTs,
                          String crtTs,
                          String completeTs,
                          char postVoidFlag,
                          Handler<AsyncResult<JsonObject>> resultHandler);

    @Fluent
    PreAuthService getPendingBalance(String gcardNbr, Handler<AsyncResult<JsonObject>> resultHandler);

    @Fluent
    PreAuthService getCurrentBalance(String gcardNbr, Handler<AsyncResult<JsonObject>> resultHandler);


    @GenIgnore
    static PreAuthService create(SQLClient sqlClient, Handler<AsyncResult<PreAuthService>> readyHandler) {
        return new PreAuthServiceImpl(sqlClient, readyHandler);
    }

   static PreAuthService createProxy(Vertx vertx, String address) {
       return null;
   }
}
