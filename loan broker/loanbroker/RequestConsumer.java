package loanbroker;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import model.loan.LoanRequest;

import java.io.IOException;

public class RequestConsumer {

    private LoanBrokerFrame loanBrokerFrame;
    private ConnectionFactory connectionFactory;

    private static RequestConsumer instance = null;

    private RequestConsumer(LoanBrokerFrame loanBrokerFrame) {
        this.loanBrokerFrame = loanBrokerFrame;
        this.connectionFactory = initConnectionFactory();
    }

    public static RequestConsumer getInstance(LoanBrokerFrame loanBrokerFrame) {
        if (instance == null) {
            instance = new RequestConsumer(loanBrokerFrame);
        }
        return instance;
    }

    public void consume(String queueName) {
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);

            channel.basicConsume(queueName, true, getDefaultConsumer(channel));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private Consumer getDefaultConsumer(Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");

                Gson gson = new Gson();
                LoanRequest loanRequest = gson.fromJson(message, LoanRequest.class);

                loanBrokerFrame.add(loanRequest);
            }
        };
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
