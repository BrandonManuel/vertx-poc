package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.ext.web.RoutingContext;

import io.vertx.ext.unit.TestSuite;
import io.vertx.ext.unit.Async;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.HttpVerticle;
import java.net.ServerSocket;
import java.lang.reflect.Method;




@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  private Integer port;
  Vertx vertx;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }

  @Test
  void testIndexhandler(VertxTestContext testContext) {
    TestSuite suite = TestSuite.create("test_suite");
    suite.test("test_suite", context -> {

      Async async1 = context.async();
      HttpClient client = vertx.createHttpClient();
      HttpClientRequest req = client.get(8080, "localhost", "/");
      req.exceptionHandler(err -> context.fail(err.getMessage()));
      req.handler(resp -> {
        context.assertEquals(200, resp.statusCode());
        async1.complete();
      });
      req.end();

      Async async2 = context.async();
      vertx.eventBus().consumer("the-address", msg -> {
        async2.complete();
      });
    });
    suite.run();
  }
}
