package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.models;

import java.util.UUID;

public class GiftCard {
    String cardNumber;
    float amount;
    UUID uuid;
    public GiftCard(String cardNumber, float amount, UUID uuid) {
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.uuid = uuid;
    }
}
