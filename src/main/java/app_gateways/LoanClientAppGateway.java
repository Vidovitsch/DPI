package app_gateways;

import message_gateways.MessageReceiverGateway;
import message_gateways.MessageSenderGateway;
import models.loan.LoanReply;
import models.loan.LoanRequest;
import serializers.LoanSerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class LoanClientAppGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private LoanSerializer serializer;

    private LoanClientAppGateway() {
        try {
            this.sender = new MessageSenderGateway("loanReply");
            this.receiver = new MessageReceiverGateway("loanRequest");
            this.serializer = new LoanSerializer();

            this.setListener();
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void sendLoanReply(LoanRequest request, LoanReply reply) {
        try {
            String json = this.serializer.requestToString(request);
            Message message = this.sender.createTextMessge(json);
            this.sender.send(message);
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public abstract void onLoanRequestArrived(LoanRequest request);

    private void setListener() {
        try {
            this.receiver.setListener(message ->
                    onLoanRequestArrived(serializer.requestFromString(message.toString())));
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }
}
