package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.impl;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import io.vertx.ext.sql.SQLClient;

import java.sql.Timestamp;
import java.util.UUID;

@ProxyGen
public interface PreAuthService {

    @Fluent
    boolean insertPreAuth(String gcardNbr,
                          Double authAmt,
                          Timestamp exprTs,
                          UUID authId,
                          UUID reqId,
                          Timestamp lastUpdatedTs,
                          Timestamp crtTs,
                          Timestamp completeTs,
                          char postVoidFlag,
                          Handler<AsyncResult<PreAuthService>> resultHandler);

    @Fluent
    double getPendingBalance(String gcardNbr, Handler<AsyncResult<PreAuthService>> resultHandler);

    @Fluent
    double getCurrentBalance(String gcardNbr, Handler<AsyncResult<PreAuthService>> resultHandler);


    @GenIgnore
    static PreAuthService create(SQLClient sqlClient, Handler<AsyncResult<PreAuthService>> readyHandler) {
        return new PreAuthServiceImpl(sqlClient, readyHandler);
    }
//
//    static PreAuthService createProxy(Vertx vertx, String address) {
//        return new PreAuthServiceImpl(vertx, address);
//    }
}
