package loanclient;

import Util.ConnectionFactoryProvider;
import com.google.gson.Gson;
import com.rabbitmq.client.*;
import model.loan.LoanReply;
import model.loan.LoanRequest;

import java.io.IOException;

public class ReplyConsumer {

    private LoanClientFrame loanClientFrame;
    private static ReplyConsumer instance = null;

    private ReplyConsumer(LoanClientFrame loanClientFrame) {
        this.loanClientFrame = loanClientFrame;
    }

    public static ReplyConsumer getInstance(LoanClientFrame loanBrokerFrame) {
        if (instance == null) {
            instance = new ReplyConsumer(loanBrokerFrame);
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
                LoanReply loanReply = gson.fromJson(message, LoanReply.class);
                LoanRequest loanRequest = loanClientFrame.findCorrelatedRequest(loanReply.getCorrelationId());
                loanClientFrame.add(loanRequest, loanReply);
            }
        };
    }
}
