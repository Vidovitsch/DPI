package services;

import util.ConnectionFactoryProvider;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

public class GenericProducer {

    private static GenericProducer instance;

    public static GenericProducer getInstance() {
        if (instance == null) {
            instance = new GenericProducer();
        }
        return instance;
    }

    public void produce(Object dto, String queueName) {
        try {
            ConnectionFactory connectionFactory = ConnectionFactoryProvider.getInstance();
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);

            String json =  new Gson().toJson(dto);
            channel.basicPublish("", queueName, new AMQP.BasicProperties(), json.getBytes());

            channel.close();
            connection.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
