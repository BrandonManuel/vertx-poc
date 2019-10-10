package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.PostgresVerticle;
import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.service.impl.PreAuthServiceImplTest;
import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.PreAuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class TestPreauthService {
  private static final Logger LOGGER = LoggerFactory.getLogger(PreAuthServiceImplTest.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    LOGGER.debug("Before test");
    vertx.deployVerticle(new PostgresVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void test_insert(Vertx vertx, VertxTestContext testContext) throws Throwable {
    LOGGER.debug("Started test");
    PreAuthService preAuthService = PreAuthService.createProxy(vertx, "gcpreauth");
    LOGGER.debug("Retreived PreAuthService");
    preAuthService.insertPreAuth("1234567890", 12.0, Timestamp.from(Instant.now()).toString(),
        UUID.randomUUID().toString(), UUID.randomUUID().toString(), Timestamp.from(Instant.now()).toString(),
        Timestamp.from(Instant.now()).toString(), Timestamp.from(Instant.now()).toString(), 'Y', result -> {
          if (result.succeeded()) {
            testContext.completeNow();
          } else {
            testContext.failed();
          }
        });

  }
}
