package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.models.GiftCard;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.UUID;

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
        routingContext.request().bodyHandler(bodyHandler -> {
            final JsonObject body = bodyHandler.toJsonObject();

            System.out.println(body);
            String uuidString;
            UUID uuid;
            String cardNumber;
            float amount;

            try {
                uuidString = body.getString("uuid");
                uuid = UUID.fromString(uuidString);
                cardNumber = body.getString("cardNumber");
                amount = body.getFloat("amount");
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