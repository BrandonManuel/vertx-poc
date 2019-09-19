package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.models;

import java.util.UUID;

public class GiftCard {
    private String cardNumber;
    private float amount;
    private UUID uuid;
    public GiftCard(String cardNumber, float amount, UUID uuid) {
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.uuid = uuid;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public float getAmount() {
        return this.amount;
    }

    public UUID getUUID() {
        return this.uuid;
    }

}
