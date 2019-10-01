package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.impl;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.PreAuthService;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.SQLClient;

public class PreAuthServiceImpl implements PreAuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreAuthServiceImpl.class);

    private SQLClient sqlClient;
    private double balance;

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
                          Handler<AsyncResult<JsonObject>> resultHandler){

        JsonArray record = new JsonArray().add(gcardNbr).add(authAmt).add(exprTs.toString()).add(authId.toString()).add(reqId.toString()).add(lastUpdatedTs.toString()).add(crtTs.toString()).add(completeTs.toString()).add(postVoidFlag);
        sqlClient.updateWithParams("INSERT INTO GC_PREAUTH VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", record, create -> {
            if(create.failed()) {
                LOGGER.error("Failed to insert preauth record", create.cause());
                resultHandler.handle(Future.failedFuture(create.cause()));
            } else {
                if(LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Record inserted into GC_PREAUTH table");
                }
                JsonObject response = new JsonObject();
                response.put("status", true);
                resultHandler.handle(Future.succeededFuture(response));
            }
        });

        return this;
    }

    @Override
    public PreAuthService getPendingBalance(String gcardNbr, Handler<AsyncResult<JsonObject>> resultHandler) {

        JsonArray record = new JsonArray().add(gcardNbr);
        sqlClient.queryWithParams("SELECT current_bal FROM gcard where gcard_nbr = trim(?)", record, create -> {
            if(create.failed()) {
                LOGGER.error("Failed to insert preauth record", create.cause());
                resultHandler.handle(Future.failedFuture(create.cause()));
            } else {
                if(LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Record inserted into GC_PREAUTH table");
                }
                balance = create.result().getResults().get(0).getDouble(0);
                JsonObject response = new JsonObject();
                response.put("balance", balance);
                resultHandler.handle(Future.succeededFuture(response));
            }
        });

        return this;
    }

    @Override
    public PreAuthServiceImpl getCurrentBalance(String gcardNbr, Handler<AsyncResult<JsonObject>> resultHandler) {
        return this;
    }

    public Double getBalance() {
        return balance;
    }
}
