package com.homedepot.hackathon.gcpreauth.vertx_gcpreauth;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Promise<String> httpVerticleDeployment = Promise.promise();

    httpVerticleDeployment.future().setHandler(ar -> {
      if (ar.succeeded()) {
        startPromise.complete();
      } else {
        startPromise.fail(ar.cause());
      }
    });

    vertx.deployVerticle(HttpVerticle.class,
            new DeploymentOptions().setInstances(1), httpVerticleDeployment);
  }
}
