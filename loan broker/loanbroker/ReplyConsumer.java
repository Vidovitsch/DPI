package loanbroker;

import Util.ConnectionFactoryProvider;
import com.google.gson.Gson;
import com.rabbitmq.client.*;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;
import model.loan.LoanReply;
import model.loan.LoanRequest;
import services.GenericProducer;

import java.io.IOException;

public class ReplyConsumer {

    private LoanBrokerFrame loanBrokerFrame;
    private static ReplyConsumer instance = null;

    private ReplyConsumer(LoanBrokerFrame loanBrokerFrame) {
        this.loanBrokerFrame = loanBrokerFrame;
    }

    public static ReplyConsumer getInstance(LoanBrokerFrame loanBrokerFrame) {
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
                BankInterestReply bankReply = gson.fromJson(message, BankInterestReply.class);
                LoanRequest loanRequest = loanBrokerFrame.findCorrelatedRequest(bankReply.getCorrelationId());
                loanBrokerFrame.add(loanRequest, bankReply);

                LoanReply loanReply = new LoanReply(bankReply.getInterest(), bankReply.getQuoteId());
                loanReply.setCorrelationId(bankReply.getCorrelationId());

                GenericProducer.getInstance().produce(loanReply, "loanReply");
            }
        };
    }
}
