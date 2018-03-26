package util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQManager {

    private Connection connection;
    private Channel channel;

    public void createQueue(String queueName) throws IOException, TimeoutException {
        this.connection = ConnectionFactoryProvider.getRMQConnectionFactory().newConnection();
        this.channel = connection.createChannel();

        this.channel.queueDeclare(queueName, true, false, false, null);

        this.channel.close();
        this.connection.close();
    }

    public void deleteQueue(String queueName) throws IOException, TimeoutException {
        this.connection = ConnectionFactoryProvider.getRMQConnectionFactory().newConnection();
        this.channel = connection.createChannel();

        this.channel.queueDelete(queueName);

        this.channel.close();
        this.connection.close();
    }

    public void createExchange(String exchangeName) throws IOException, TimeoutException {
        this.connection = ConnectionFactoryProvider.getRMQConnectionFactory().newConnection();
        this.channel = connection.createChannel();

        this.channel.exchangeDeclare(exchangeName, "direct", true);

        this.channel.close();
        this.connection.close();
    }

    public void bindQueue(String queueName, String exchangeName, String routingKey) throws IOException, TimeoutException {
        this.connection = ConnectionFactoryProvider.getRMQConnectionFactory().newConnection();
        this.channel = connection.createChannel();

        this.channel.queueBind(queueName, exchangeName, routingKey);

        this.channel.close();
        this.connection.close();
    }
}
