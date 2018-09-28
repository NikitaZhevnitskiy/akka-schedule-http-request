package com.zhenik.akka.http;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Requester extends AbstractActor {

    // https://doc.akka.io/docs/akka-http/current/client-side/request-level.html
    final Http http = Http.get(context().system());

    //logger
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    // last readed entity - like a state, not accessible in sync manner
    int lastReading = 1;
    final String targetHostUrl;

    //constructor
    public Requester(final String targetHostUrl) {
        this.targetHostUrl = targetHostUrl;
    }

    //props to init actor
    public static Props props(final String targetHostUrl) {
        return Props.create(Requester.class, targetHostUrl);
    }

    @Override
    public void preStart() {
        log.info("Requester started");
    }

    // here is a behaviour on messages
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, r -> handleResponse(fetch(lastReading++)))
                .build();
    }


    CompletionStage<HttpResponse> fetch(int dynamicQuery) {
        return http.singleRequest(HttpRequest.create(targetHostUrl + "/models?id=" + dynamicQuery));
    }

    void handleResponse(CompletionStage<HttpResponse> responseCompletionStage) {
        try {
            HttpResponse response = responseCompletionStage.toCompletableFuture().get(5, TimeUnit.SECONDS);
            System.out.println(response);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }


    // message
    static final class Request {
        Request() {
        }
    }

}
