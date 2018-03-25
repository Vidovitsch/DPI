package util;

import com.rabbitmq.jms.admin.RMQDestination;

import javax.jms.Destination;

public class DestinationProvider {

    public static Destination getInstance(String destinationName, String queueName) {
        return createConnectionFactory(destinationName, queueName);
    }

    private static Destination createConnectionFactory(String destinationName, String queueName) {
        RMQDestination destination = new RMQDestination();
        destination.setDestinationName(destinationName);
        destination.setAmqp(true);
        destination.setAmqpQueueName(queueName);

        return destination;
    }
}
