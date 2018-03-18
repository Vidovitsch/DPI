package loanclient;

import Util.ConnectionFactoryProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.UUID;

public class RequestProducer {

    private static RequestProducer instance;

    public static RequestProducer getInstance() {
        if (instance == null) {
            instance = new RequestProducer();
        }
        return instance;
    }

    public void produce(Object request, String queueName) {
        try {
            ConnectionFactory connectionFactory = ConnectionFactoryProvider.getInstance();
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);

            Gson gson = new Gson();
            String json = gson.toJson(request);

            String correlationId = UUID.randomUUID().toString();
            channel.basicPublish("", queueName, new AMQP.BasicProperties().builder().correlationId(correlationId).build(), json.getBytes());

            channel.close();
            connection.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
