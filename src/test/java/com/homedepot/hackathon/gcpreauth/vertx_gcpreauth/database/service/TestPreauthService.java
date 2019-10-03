package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.PostgresVerticle;
import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.PreAuthService;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class TestPreauthService {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new PostgresVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }

  @Test
  void test_insert(Vertx vertx, VertxTestContext testContext) throws Throwable {
    PreAuthService preAuthService = PreAuthService.createProxy(vertx, "gcpreauth");
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

  @Test
  public void testGetPendingBalance(Vertx vertx, VertxTestContext testContext) {
    PreAuthService preAuthService = PreAuthService.createProxy(vertx, "gcpreauth");
    preAuthService.getPendingBalance("140123044100", resultHandler -> {
      if (resultHandler.failed()) {
        testContext.failNow(resultHandler.cause());
      } else {
        Assert.assertTrue(resultHandler.result().getDouble("balance") == 7000.0);
        System.out.println(resultHandler.result().getString("current_bal"));
        testContext.completeNow();
      }
    });
  }
}
