package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.service.impl;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.MainVerticle;
import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.impl.PreAuthService;
import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.impl.PreAuthServiceImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.serviceproxy.ServiceBinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@ExtendWith(VertxExtension.class)
public class PreAuthServiceImplTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreAuthServiceImplTest.class);

    @BeforeEach
    void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
    }

    @Test
    public void testGetGiftCard(Vertx vertx, VertxTestContext testContext) {
        SQLClient sqlClient = PostgreSQLClient.createShared(vertx, new JsonObject().put("host", "ld09245.homedepot.com")
                .put("database", "postgres").put("username", "postgres").put("password", "cor3services!"));

        PreAuthServiceImpl preAuthServiceImpl = new PreAuthServiceImpl(sqlClient, ready -> {
            if (ready.failed()) {
                testContext.failed();
            } else {
                ServiceBinder binder = new ServiceBinder(vertx).setAddress("chkauthaddress");
                binder.registerLocal(PreAuthService.class, ready.result());
                if(LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Starting HTTP Verticle");
                }
                testContext.completeNow();
            }});

        preAuthServiceImpl.insertPreAuth("123",1d, Timestamp.from(Instant.now()), UUID.randomUUID(), UUID.randomUUID(),
                Timestamp.from(Instant.now()), Timestamp.from(Instant.now()), Timestamp.from(Instant.now()), 'y', ready -> {
                    if (ready.failed()) {
                        testContext.failed();
                    } else {
                        ServiceBinder binder = new ServiceBinder(vertx).setAddress("chkauthaddress");
                        binder.registerLocal(PreAuthService.class, ready.result());
                        if(LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Starting HTTP Verticle");
                        }
                        testContext.completeNow();
                    }
                });
    }

}