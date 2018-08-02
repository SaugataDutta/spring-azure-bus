/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.falabella;

import com.microsoft.azure.servicebus.*;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ServiceBusSampleApplication implements CommandLineRunner {

    @Autowired
    private TopicClient topicClient;
    @Autowired
    private SubscriptionClient subscriptionClient;

    public static void main(String[] args) {
        SpringApplication.run(ServiceBusSampleApplication.class);
    }

    public void run(String... var1) throws ServiceBusException, InterruptedException {
        sendTopicMessage();
        receiveSubscriptionMessage();
    }


    private void sendTopicMessage() throws ServiceBusException, InterruptedException {
        final String messageBody = "topic message";
        System.out.println("Sending message: " + messageBody);
        final Message message = new Message(messageBody.getBytes(StandardCharsets.UTF_8));
        topicClient.send(message);
        topicClient.close();
    }

    private void receiveSubscriptionMessage() throws ServiceBusException, InterruptedException {
        subscriptionClient.registerMessageHandler(new MessageHandler(), new MessageHandlerOptions());

        TimeUnit.SECONDS.sleep(5);
        subscriptionClient.close();
    }

    static class MessageHandler implements IMessageHandler {
        public CompletableFuture<Void> onMessageAsync(IMessage message) {
            final String messageString = new String(message.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received message: " + messageString);
            return CompletableFuture.completedFuture(null);
        }

        public void notifyException(Throwable exception, ExceptionPhase phase) {
            System.out.println(phase + " encountered exception:" + exception.getMessage());
        }
    }
}
