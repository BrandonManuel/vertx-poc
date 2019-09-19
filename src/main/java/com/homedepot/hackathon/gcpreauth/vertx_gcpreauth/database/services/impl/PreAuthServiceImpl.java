package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.impl;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.SQLClient;

import java.sql.Timestamp;
import java.util.UUID;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.PreAuthService;

public class PreAuthServiceImpl implements PreAuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreAuthServiceImpl.class);

    private SQLClient sqlClient;

    public PreAuthServiceImpl(SQLClient sqlClient, Handler<AsyncResult<PreAuthService>> readyHandler) {
        this.sqlClient = sqlClient;

        this.sqlClient.getConnection(ar -> {
            if(ar.failed()) {
                LOGGER.error("Failed to connect to database", ar.cause());
                readyHandler.handle(Future.failedFuture(ar.cause()));
            } else {
                if(LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Connected now!");
                }
                ar.result().close();
                readyHandler.handle(Future.succeededFuture(this));
            }
        });
    }

    @Override
    @Fluent
    public PreAuthService insertPreAuth(String gcardNbr,
                          Double authAmt,
                          String exprTs,
                          String authId,
                          String reqId,
                          String lastUpdatedTs,
                          String crtTs,
                          String completeTs,
                          char postVoidFlag,
                          Handler<AsyncResult<Void>> resultHandler){

        JsonArray record = new JsonArray().add(gcardNbr).add(authAmt).add(exprTs.toString()).add(authId.toString()).add(reqId.toString()).add(lastUpdatedTs.toString()).add(crtTs.toString()).add(completeTs.toString()).add(postVoidFlag);
        sqlClient.updateWithParams("INSERT INTO GC_PREAUTH VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", record, create -> {
            if(create.failed()) {
                LOGGER.error("Failed to insert preauth record", create.cause());
                resultHandler.handle(Future.failedFuture(create.cause()));
            } else {
                if(LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Record inserted into GC_PREAUTH table");
                }
                resultHandler.handle(Future.succeededFuture());
            }
        });

        return this;
    }

    @Override
    public PreAuthService getPendingBalance(String gcardNbr, Handler<AsyncResult<Void>> resultHandler) {
        return this;
    }

    @Override
    public PreAuthService getCurrentBalance(String gcardNbr, Handler<AsyncResult<Void>> resultHandler) {
        return this;
    }
}
