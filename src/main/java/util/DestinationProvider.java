package util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.jms.admin.RMQDestination;

import javax.jms.Destination;
import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class DestinationProvider {

    public static Destination getInstance(String destinationName, String routingKey)
            throws IOException, TimeoutException {

        return createDestination(destinationName, routingKey);
    }

    private static Destination createDestination(String destinationName, String routingKey)
            throws IOException, TimeoutException {

        String exchangeName = "jms.durable.queues";
        ensureDestinationAvailability(exchangeName, destinationName, routingKey);

        RMQDestination destination =  new RMQDestination(destinationName, exchangeName, routingKey, destinationName);
        destination.setAmqp(true);

        return destination;
    }

    private static void ensureDestinationAvailability(String exchangeName, String queueName, String routingKey)
            throws IOException, TimeoutException {

        Connection connection = ConnectionFactoryProvider.getRMQConnectionFactory().newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(exchangeName, "direct", true);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);
    }
}
