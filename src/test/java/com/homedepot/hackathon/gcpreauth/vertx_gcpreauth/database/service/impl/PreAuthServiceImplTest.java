package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.PreAuthService;
import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.impl.PreAuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class PreAuthServiceImplTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreAuthServiceImplTest.class);

    private SQLClient sqlClient;
    PreAuthService preAuthService;

    @BeforeEach
    void deploy_verticle(Vertx vertx, VertxTestContext testContext) throws InterruptedException {
        sqlClient = PostgreSQLClient.createShared(vertx, new JsonObject().put("host", "localhost")
                .put("database", "postgres").put("username", "postgres").put("password", "cor3services!"));
        testContext.assertComplete(Future.future(complete -> {
            preAuthService = new PreAuthServiceImpl(sqlClient, ready -> {
                LOGGER.debug("Created Service Impl instance");
                if (ready.succeeded()) {
                    preAuthService = ready.result();
                }
            });
            testContext.completeNow();
        }));
    }

    @Test
    public void testGetGiftCard(Vertx vertx, VertxTestContext testContext) {
        testContext.completeNow();
        // PreAuthServiceImpl preAuthServiceImpl = new PreAuthServiceImpl(sqlClient,
        // ready -> {
        // if (ready.failed()) {
        // testContext.failed();
        // } else {
        // // ServiceBinder binder = new
        // ServiceBinder(vertx).setAddress("chkauthaddress");
        // // binder.registerLocal(PreAuthService.class, ready.result());
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("Starting HTTP Verticle");
        // }
        // testContext.completeNow();
        // }
        // });

        // PreAuthService preAuthService = preAuthServiceImpl.insertPreAuth("123", 1d,
        // Timestamp.from(Instant.now()).toString(), UUID.randomUUID().toString(),
        // UUID.randomUUID().toString(),
        // Timestamp.from(Instant.now()).toString(),
        // Timestamp.from(Instant.now()).toString(),
        // Timestamp.from(Instant.now()).toString(), 'y', ready -> {
        // if (ready.failed()) {
        // testContext.failed();
        // } else {
        // testContext.completeNow();
        // }
        // });
    }

    @Test
    public void testGetPendingBalance(Vertx vertx, VertxTestContext testContext) {
        preAuthService.getPendingBalance("140123044100", balanceResult -> {
            if (balanceResult.failed()) {
                testContext.failNow(balanceResult.cause());
            } else {
                LOGGER.debug("Query response returned");
                assertTrue(Integer.parseInt(balanceResult.result().getString("current_bal")) == 7000);
                testContext.completeNow();
            }
        });
    }

}
