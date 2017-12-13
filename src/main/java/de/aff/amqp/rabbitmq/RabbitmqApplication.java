package de.aff.amqp.rabbitmq;

import de.aff.amqp.rabbitmq.messaging.Receiver;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


//Spring AMQP requires the Queue, the TopicExchange, and the Binding
// are declared as top level Spring beans in order to be set up properly.
@SpringBootApplication
public class RabbitmqApplication {

	//We will listen for messages on the "spring-boot" queue
	final static String queueName = "spring-boot";

	//creates an AMQP queue (just like JMS queue, messages are sent to only one consumer,
	// but producer doesn't produces to the queue, he produces first to the exchange)
	@Bean
	Queue queue() {
		return new Queue(queueName, false);
	}

	//creates a topic exchange (can send the message to more than one queue, just like JMS topic)
	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	//binding the queue and exchange together
	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(queueName);
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
											 MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		//The bean defined in the listenerAdapter() method is registered as a message listener in the container
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		//Receiver class is a POJO, it needs to be wrapped in the MessageListenerAdapter,
		// where we specify to invoke receiveMessage method
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqApplication.class, args);
	}
}
