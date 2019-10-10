package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.PreAuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HttpVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpVerticle.class);

    private PreAuthService service;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer httpServer = vertx.createHttpServer();

        this.service = PreAuthService.createProxy(vertx, "gcpreauth");

        Router router = Router.router(vertx);
        router.get("/").handler(this::indexHandler);
        router.post("/preauth").handler(this::preauthHandler);
        httpServer.requestHandler(router).listen(8080, http -> {
            if (http.succeeded()) {
                startPromise.complete();
            } else {
                startPromise.fail(http.cause());
            }
        });
    }

    private void indexHandler(RoutingContext routingContext) {
        routingContext.response().end("Preauth Service");
    }

    private void preauthHandler(RoutingContext routingContext) {
        routingContext.request().bodyHandler(bodyHandler -> {
            final JsonObject body = bodyHandler.toJsonObject();

            LOGGER.debug("Received post request with body {}", body);
            String uuid;
            String cardNumber;
            double amount;
            try {
                uuid = body.getString("uuid");
                cardNumber = body.getString("gcard_nbr");
                amount = body.getDouble("amount");

                // Fetch pending balance
                // Fetch current balance
                // if current balance - pending balance > amount else not authorized

                this.service.getCurrentBalance(cardNumber, currentBalanceResult -> {
                    if (currentBalanceResult.failed()) {
                        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                                .setStatusCode(503).end("{\"status\":\"failure\", \"message\":\"Unknowd error\"}");
                        return;
                    }
                    final double currentBalance = currentBalanceResult.result().getDouble("BALANCE");
                    this.service.getPendingBalance(cardNumber, pendingBalanceResult -> {
                        if (pendingBalanceResult.failed()) {
                            routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                                    .setStatusCode(503).end("{\"status\":\"failure\", \"message\":\"Unknowd error\"}");
                            return;
                        }
                        final double pendingBalance = pendingBalanceResult.result().getDouble("PREAUTH_AMOUNT");

                        if ((currentBalance - pendingBalance) >= amount) {
                            // Do the insert
                            this.service.insertPreAuth(cardNumber, amount, Timestamp.from(Instant.now()).toString(),
                                    UUID.randomUUID().toString(), uuid, Timestamp.from(Instant.now()).toString(),
                                    Timestamp.from(Instant.now()).toString(), Timestamp.from(Instant.now()).toString(),
                                    'Y', insert -> {
                                        if (insert.failed()) {
                                            routingContext.response()
                                                    .putHeader("content-type", "application/json; charset=utf-8")
                                                    .setStatusCode(503).end("{\"status\":\"failure\", \"message\":"
                                                            + insert.cause().getMessage() + "}");
                                        } else {
                                            routingContext.response()
                                                    .putHeader("content-type", "application/json; charset=utf-8")
                                                    .setStatusCode(200)
                                                    .end("{\"status\":\"success\", \"message\":\"Gift card successfully verified\"}");
                                        }
                                    });
                        } else {
                            routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                                    .setStatusCode(503)
                                    .end("{\"status\":\"failure\", \"message\":\"Unauthorized\"}");
                            return;
                        }

                    });
                });

            } catch (NullPointerException e) {
                routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                        .setStatusCode(500).end("{\"status\":\"failure\", \"message\":\"Parameter not found\"}");
                return;
            } catch (ClassCastException e) {
                routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                        .setStatusCode(500).end("{\"status\":\"failure\", \"message\":\"Parameter not assignable\"}");
                return;
            } catch (IllegalArgumentException e) {
                routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                        .setStatusCode(500).end("{\"status\":\"failure\", \"message\":\"Invalid UUID\"}");
                return;
            }

        });

    }
}