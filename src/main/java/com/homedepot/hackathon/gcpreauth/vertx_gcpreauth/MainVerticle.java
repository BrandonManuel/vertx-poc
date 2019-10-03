package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth;

import com.homedepot.hackathon.gcpreauth.vertx_gcpreauth.database.PostgresVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Promise<String> httpVerticleDeployment = Promise.promise();
    Promise<String> postgresVerticleDeployment = Promise.promise();

    httpVerticleDeployment.future().setHandler(ar -> {
      if (ar.succeeded()) {
        System.out.println("HTTP VERTICLE DEPLOYED");
        startPromise.complete();
      } else {
        startPromise.fail(ar.cause());
      }
    });

    postgresVerticleDeployment.future().setHandler(postgres -> {
      if(postgres.succeeded()) {
        System.out.println("POSTGRES VERTICLE DEPLOYED");
        vertx.deployVerticle(HttpVerticle.class,
                new DeploymentOptions().setInstances(1), httpVerticleDeployment);
      } else {
        startPromise.fail(postgres.cause());
      }
    });

    vertx.deployVerticle(PostgresVerticle.class, new DeploymentOptions().setInstances(1), postgresVerticleDeployment);
  }
}
