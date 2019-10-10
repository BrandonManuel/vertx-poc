package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(TestMainVerticle.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }

  @Test
  void testIndexhandler(Vertx vertx, VertxTestContext testContext) {
    WebClient client = WebClient.create(vertx);

    LOGGER.debug("Test started");

    client.get(8080, "localhost", "/").as(BodyCodec.string())
        .send(testContext.succeeding(response -> testContext.verify(() -> {
          LOGGER.debug("Received response from HTTP Verticle for / {}", response.body());
          assertTrue(response.body().equalsIgnoreCase("Preauth Service"));
          testContext.completeNow();
        })));
  }

  @Test
  void testPreAuthhandler(Vertx vertx, VertxTestContext testContext) {
    WebClient client = WebClient.create(vertx);

    LOGGER.debug("Test started");

    JsonObject body = new JsonObject();
    body.put("uuid", UUID.randomUUID().toString());
    body.put("gcard_nbr", "1234567890");
    body.put("amount", 30.0);
    client.post(8080, "localhost", "/preauth").as(BodyCodec.jsonObject()).sendJsonObject(body, response -> {
      if(response.failed()) {
        LOGGER.error("Failed to insert auth", response.cause());
        testContext.failNow(response.cause());
      }
      LOGGER.debug("Received repsonse {}", response.result().body());  
      testContext.completeNow();
    });
    ;
  }
}
