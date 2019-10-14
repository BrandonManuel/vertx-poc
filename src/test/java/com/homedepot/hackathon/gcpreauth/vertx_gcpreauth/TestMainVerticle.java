package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth;

import static io.vertx.junit5.web.TestRequest.bodyResponse;
import static io.vertx.junit5.web.TestRequest.jsonBodyResponse;
import static io.vertx.junit5.web.TestRequest.statusCode;
import static io.vertx.junit5.web.TestRequest.testRequest;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.junit5.web.VertxWebClientExtension;
import io.vertx.junit5.web.WebClientOptionsInject;

@ExtendWith({ VertxExtension.class, VertxWebClientExtension.class })
public class TestMainVerticle {
  @WebClientOptionsInject
  public WebClientOptions options = new WebClientOptions().setDefaultHost("localhost").setDefaultPort(8080);

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
  void testIndexhandler(WebClient client, Vertx vertx, VertxTestContext testContext) {
    LOGGER.debug("Test started");
    testRequest(client, HttpMethod.GET, "/")
        .expect(statusCode(200), bodyResponse(Buffer.buffer("Preauth Service"), "text/plain")).send(testContext);
  }

  @Test
  @RepeatedTest(3)
  void testPreAuthhandler(WebClient client, Vertx vertx, VertxTestContext testContext) {

    LOGGER.debug("Test started");

    JsonObject requestBody = new JsonObject().put("uuid", UUID.randomUUID().toString()).put("gcard_nbr", "140123041400")
        .put("amount", 1000.0);

    JsonObject expectedResponse = new JsonObject().put("status", "failure").put("message", "Unknown error");;
    testRequest(client, HttpMethod.POST, "/preauth").expect(jsonBodyResponse(expectedResponse)).sendJson(requestBody,
        testContext);
  }
}
