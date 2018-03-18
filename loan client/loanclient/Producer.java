package loanclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import model.loan.LoanRequest;

import java.io.IOException;
import java.util.UUID;

public class Producer {

    private ConnectionFactory connectionFactory;


    private static Producer instance;

    private Producer() {
        this.connectionFactory = initConnectionFactory();
    }

    public static Producer getInstance() {
        if (instance == null) {
            instance = new Producer();
        }
        return instance;
    }

    public void produce(LoanRequest loanRequest, String queueName) {
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(loanRequest);

            channel.basicPublish("", queueName, new AMQP.BasicProperties().builder().correlationId(UUID.randomUUID().toString()).build(), json.getBytes());

            channel.close();
            connection.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private ConnectionFactory initConnectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.24.77");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        return connectionFactory;
    }
}
