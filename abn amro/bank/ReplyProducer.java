package bank;

import Util.ConnectionFactoryProvider;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import model.bank.BankInterestReply;

import java.io.IOException;
import java.util.UUID;

public class ReplyProducer {

    private static ReplyProducer instance;

    public static ReplyProducer getInstance() {
        if (instance == null) {
            instance = new ReplyProducer();
        }
        return instance;
    }

    public void produce(BankInterestReply bankInterestReply, String queueName) {
        try {
            ConnectionFactory connectionFactory = ConnectionFactoryProvider.getInstance();

            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);

            Gson gson = new Gson();
            String json = gson.toJson(bankInterestReply);

            channel.basicPublish("", queueName, new AMQP.BasicProperties().builder().correlationId(UUID.randomUUID().toString()).build(), json.getBytes());

            channel.close();
            connection.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
