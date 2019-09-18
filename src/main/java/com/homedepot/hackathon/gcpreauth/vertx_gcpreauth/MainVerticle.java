package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth;

import java.util.UUID;

import io.netty.handler.codec.http.HttpResponseEncoder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    // Router router = Router.router(vertx);

    // router.route("/ping").handler(routingContext -> {
    //     HttpServerResponse response = routingContext.response();
    //     response.putHeader("content-type", "text/html").end("this is a response!");

    // });
    Router router = Router.router(vertx);

    // Bind "/" to our hello message - so we are still compatible.
    router.post("/ping").handler(routingContext -> {
      routingContext.request().bodyHandler(bodyHandler -> {
        JsonObject json = bodyHandler.toJsonObject();
        UUID reqUUID;

        reqUUID = UUID.fromString(json.getString("uuid"));
        System.out.println(reqUUID);
        // System.out.println(json.getString("uuid"));


        HttpServerResponse response = routingContext.response();
        response
            .putHeader("content-type", "application/json")
            .end("{\"status\" : 200}");
      });

    });

    vertx
        .createHttpServer()
        .requestHandler(router).listen(8080, http -> {
        if (http.succeeded()) {
            startPromise.complete();
            System.out.println("HTTP server started on port 8080");
        } else {
            startPromise.fail(http.cause());
        }
        });
  }
}
