package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.services.PreAuthService;
import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.models.GiftCard;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HttpVerticle extends AbstractVerticle {

    private PreAuthService service;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer httpServer = vertx.createHttpServer();

        this.service = PreAuthService.createProxy(vertx, "gcpreauth");

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
        routingContext.request().bodyHandler(bodyHandler -> {
            final JsonObject body = bodyHandler.toJsonObject();

            System.out.println(body);
            String uuidString;
            String uuid;
            String cardNumber;
            double amount;

            try {
                // uuidString = body.getString("uuid");
                uuid = body.getString("uuid");
                cardNumber = body.getString("gcard_nbr");
                amount = body.getDouble("amount");

                System.out.println(uuid + ", " + cardNumber + ", " + amount);

                this.service.insertPreAuth(
                    cardNumber, 
                    amount,
                    Timestamp.from(Instant.now()).toString(), 
                    UUID.randomUUID().toString(), 
                    uuid, 
                    Timestamp.from(Instant.now()).toString(), 
                    Timestamp.from(Instant.now()).toString(), 
                    Timestamp.from(Instant.now()).toString(), 
                    'Y', 
                    insert -> {
                        if (insert.failed()) {
                            System.out.println("Insert failed!!!");
                            System.err.println(insert.cause());
                        } else {
                            System.out.println("Insert success!!!");
                        }
                });

            } catch (NullPointerException e) {
              routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                      .setStatusCode(400).end("{\"status\":\"failure\", \"message\":\"Parameter not found\"}");
              return;
            } catch (ClassCastException e) {
                routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                    .setStatusCode(400).end("{\"status\":\"failure\", \"message\":\"Parameter not assignable\"}");
                return;
            } catch (IllegalArgumentException e) {
                routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                        .setStatusCode(400).end("{\"status\":\"failure\", \"message\":\"Invalid UUID\"}");
                return;
            }

            System.out.println("UUID: " + uuid);
            System.out.println("Card number: " + cardNumber);
            System.out.println("Amount: " + amount);

            GiftCard giftCard = new GiftCard(cardNumber, amount, uuid);

            routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                    .setStatusCode(200).end("{\"status\":\"success\", \"message\":\"Gift card successfully verified\"}");
        });

    }
}