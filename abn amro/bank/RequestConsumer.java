package bank;

import Util.ConnectionFactoryProvider;
import com.google.gson.Gson;
import com.rabbitmq.client.*;
import model.bank.BankInterestRequest;
import model.loan.LoanRequest;

import java.io.IOException;

public class RequestConsumer {

    private static RequestConsumer instance = null;

    public static RequestConsumer getInstance() {
        if (instance == null) {
            instance = new RequestConsumer();
        }
        return instance;
    }

    public void consume(String queueName) {
        try {
            ConnectionFactory connectionFactory = ConnectionFactoryProvider.getInstance();
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
                BankInterestRequest bankInterestRequest = gson.fromJson(message, BankInterestRequest.class);

                System.out.println(bankInterestRequest.getAmount());
            }
        };
    }
}
