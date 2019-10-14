package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.PreAuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
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
        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/plain").end("Preauth Service");
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
                        sendErrorResponse(routingContext);
                        return;
                    }
                    final double currentBalance = currentBalanceResult.result().getDouble("BALANCE");
                    this.service.getPendingBalance(cardNumber, pendingBalanceResult -> {
                        if (pendingBalanceResult.failed()) {
                            sendErrorResponse(routingContext);
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
                                            sendErrorResponse(routingContext);
                                        } else {
                                            sendSuccessResponse(routingContext);
                                        }
                                    });
                        } else {
                            sendErrorResponse(routingContext, "Unauthorized");
                            return;
                        }

                    });
                });

            } catch (NullPointerException e) {
                sendErrorResponse(routingContext);
                return;
            } catch (ClassCastException e) {
                sendErrorResponse(routingContext);
                return;
            } catch (IllegalArgumentException e) {
                sendErrorResponse(routingContext);
                return;
            }

        });

    }

    private void sendErrorResponse(RoutingContext routingContext) {
        sendErrorResponse(routingContext, "Unknown error");
    }

    private void sendSuccessResponse(RoutingContext routingContext) {
        JsonObject successResponse = new JsonObject().put("status", "success").put("message",
                "Gift card successfully verified");
        sendResponse(routingContext, 200, successResponse);
    }

    private void sendErrorResponse(RoutingContext routingContext, String message) {
        JsonObject errorResponse = new JsonObject().put("status", "failure").put("message", message);
        sendResponse(routingContext, 503, errorResponse);
    }

    private void sendResponse(RoutingContext routingContext, int status, JsonObject jsonObject) {
        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setStatusCode(status).end(Buffer.buffer(jsonObject.encode()));
    }
}