package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.PreAuthService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.unit.TestContext;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class PreAuthServiceImplTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreAuthServiceImplTest.class);

    private SQLClient sqlClient;
    PreAuthService preAuthService;

    @BeforeEach
    void create_service(Vertx vertx, VertxTestContext testContext) throws InterruptedException {
        sqlClient = PostgreSQLClient.createShared(vertx, new JsonObject().put("host", "localhost")
                .put("database", "postgres").put("username", "postgres").put("password", "cor3services!"));

        PreAuthService.create(sqlClient, ready -> {
            LOGGER.debug("Created Service Impl instance");
            if (ready.succeeded()) {
                preAuthService = ready.result();
            }
            testContext.completeNow();
        });

        vertx.exceptionHandler(exception -> {
            testContext.failNow(exception);
        });
    }

    @Test
    public void testInsertPreAuth(Vertx vertx, TestContext testContext) {

        preAuthService.insertPreAuth("140123041400", 100.0, Timestamp.from(Instant.now()).toString(),
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), Timestamp.from(Instant.now()).toString(),
                Timestamp.from(Instant.now()).toString(), Timestamp.from(Instant.now()).toString(), 'y', ready -> {
                    if (ready.failed()) 
                        testContext.fail();
                });
    }

    @Test
    public void testGetZeroPendingBalance(Vertx vertx, TestContext testContext) {
        preAuthService.getPendingBalance("140123044100", balanceResult -> {
            try {
                if (balanceResult.failed()) {
                    LOGGER.debug("Failed");
                    testContext.fail(balanceResult.cause());
                } else {
                    LOGGER.debug("Query response returned {}", balanceResult);

                    assertTrue(balanceResult.result().getDouble("PREAUTH_AMOUNT") == 0.0);
                }
            } catch (Exception e) {
                testContext.fail(e);
            }
        });
    }

    @Test
    public void testGetNonZeroPendingBalance(Vertx vertx, VertxTestContext testContext) {
        preAuthService.getPendingBalance("140123041400", balanceResult -> {
            if (balanceResult.failed()) {
                LOGGER.debug("Failed");
                testContext.failNow(balanceResult.cause());
            } else {
                LOGGER.debug("Query response returned {}", balanceResult);
                Assertions.assertTrue(balanceResult.result().getDouble("PREAUTH_AMOUNT") > 0.0);
                testContext.completeNow();
            }
        });
    }

    @Test
    public void testGetCurrentBalance(Vertx vertx, VertxTestContext testContext) {
        preAuthService.getCurrentBalance("140123041400", balanceResult -> {
            if (balanceResult.failed()) {
                LOGGER.debug("Failed");
                testContext.failNow(balanceResult.cause());
            } else {
                LOGGER.debug("Query response returned {}", balanceResult);
                Assertions.assertTrue(balanceResult.result().getDouble("BALANCE") == 7000.0);
                testContext.completeNow();
            }
        });
    }

}
