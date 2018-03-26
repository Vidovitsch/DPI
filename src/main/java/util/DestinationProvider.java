package util;

import com.rabbitmq.jms.admin.RMQDestination;

import javax.jms.Destination;
import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class DestinationProvider {

    private static RabbitMQManager rmqManager = new RabbitMQManager();

    public static Destination getInstance(String destinationName, String routingKey)
            throws IOException, TimeoutException {

        return createDestination(destinationName, routingKey);
    }

    private static Destination createDestination(String destinationName, String routingKey)
            throws IOException, TimeoutException {

        String exchangeName = "jms.durable.queues";
        rmqManager.createExchange(exchangeName);
        rmqManager.createQueue(destinationName);
        rmqManager.bindQueue(destinationName, exchangeName, destinationName);

        RMQDestination destination =  new RMQDestination(destinationName, exchangeName, routingKey, destinationName);
        destination.setAmqp(true);

        return destination;
    }
}
