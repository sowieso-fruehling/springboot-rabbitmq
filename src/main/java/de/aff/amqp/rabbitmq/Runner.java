package de.aff.amqp.rabbitmq;

import de.aff.amqp.rabbitmq.messaging.Receiver;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
public class Runner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final Receiver receiver;
    private final ConfigurableApplicationContext context;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Sending message...");
        //we have to set exchange, otherwise default exchange will be used ( and this will actually work if
        // routing key is the same as the queue's name
        rabbitTemplate.setExchange(RabbitmqApplication.EXCHANGE_NAME);
        rabbitTemplate.convertAndSend(RabbitmqApplication.QUEUE_NAME, "Hello from RabbitMQ!");
        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
        context.close();
    }

}