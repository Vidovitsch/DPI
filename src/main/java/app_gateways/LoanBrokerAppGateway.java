package app_gateways;

import message_gateways.MessageReceiverGateway;
import message_gateways.MessageSenderGateway;
import models.loan.LoanReply;
import models.loan.LoanRequest;
import serializers.LoanSerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class LoanBrokerAppGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private LoanSerializer serializer;

    private LoanBrokerAppGateway() {
        try {
            this.sender = new MessageSenderGateway("loanRequest");
            this.receiver = new MessageReceiverGateway("loanReply");
            this.serializer = new LoanSerializer();

            this.setListener();
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void applyForLoan(LoanRequest request) {
        try {
            String json = this.serializer.requestToString(request);
            Message message = this.sender.createTextMessge(json);
            this.sender.send(message);
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public abstract void onLoanReplyArrived(LoanRequest request, LoanReply reply);

    private void setListener() {
        try {
            this.receiver.setListener(message ->
                    onLoanReplyArrived(null, serializer.replyFromString(message.toString())));
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }
}
