package com.zhenik.akka.http;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.ExecutionContextExecutor;

import java.time.Duration;

public class Application {

    public static void main(String[] args) {

        final ActorSystem system = ActorSystem.create();
        final ExecutionContextExecutor contextExecutor = system.dispatcher();
        final Config config = ConfigFactory.load("application.conf");
        final String targetUrl = config.getString("target-host-url");

        final ActorRef requesterActor = system.actorOf(Requester.props(targetUrl), "requster");

        system.scheduler()
                .schedule(
                        Duration.ZERO,
                        Duration.ofSeconds(5),
                        requesterActor,
                        new Requester.Request(),
                        contextExecutor,
                        ActorRef.noSender()
                );
    }
}
