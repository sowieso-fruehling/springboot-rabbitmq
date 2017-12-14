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


// Spring AMQP requires the Queue, the TopicExchange, and the Binding
// are declared as top level Spring beans in order to be set up properly.
// But ... read the comment on top of exchange() method
@SpringBootApplication
public class RabbitmqApplication {

	//We will listen for messages on the "spring-boot" queue
	final static String QUEUE_NAME = "spring-boot";

	private final static String ROUTING_KEY = "foo.bar";

	final static String EXCHANGE_NAME = "spring-boot-exchange";

	//creates an AMQP queue (just like JMS queue, messages are sent to only one consumer,
	// but producer doesn't produces to the queue, he produces first to the exchange)
	@Bean
	Queue queue() {
		return new Queue(QUEUE_NAME, false);
	}

	//creates a exchange (can send the message to more than one queue, just like JMS topic)
	//For simple use cases we don't even need to create exchange and binding ( every queue created is automatically
	//binded to default rabbit exchange, with queue's name as routing key
	@Bean
	TopicExchange exchange() {
		return new TopicExchange(EXCHANGE_NAME);
	}

	//binds the queue and the exchange together
	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).
				to(exchange).
				with(ROUTING_KEY);
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
											 MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(QUEUE_NAME);
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
