package loanbroker;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import model.bank.BankInterestRequest;
import model.loan.LoanRequest;

import java.io.IOException;
import java.util.UUID;

public class RequestProducer {

    private ConnectionFactory connectionFactory;

    private static RequestProducer instance;

    private RequestProducer() {
        this.connectionFactory = initConnectionFactory();
    }

    public static RequestProducer getInstance() {
        if (instance == null) {
            instance = new RequestProducer();
        }
        return instance;
    }

    public void produce(BankInterestRequest bankInterestRequest, String queueName) {
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);

            Gson gson = new Gson();
            String json = gson.toJson(bankInterestRequest);

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
