package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HttpVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer httpServer = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.post("/").handler(this::indexHandler);
        httpServer.requestHandler(router).listen(8080, http -> {
            if (http.succeeded()) {
                startPromise.complete();
            } else {
                startPromise.fail(http.cause());
            }
        });
    }

    private void indexHandler(RoutingContext routingContext) {
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end("{\"status\":\"success\"}");
    }
}