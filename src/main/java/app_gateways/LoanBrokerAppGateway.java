package app_gateways;

import listeners.BankRequestListener;
import listeners.LoanReplyListener;
import message_gateways.MessageReceiverGateway;
import message_gateways.MessageSenderGateway;
import models.loan.LoanRequest;
import serializers.BankSerializer;
import serializers.LoanSerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoanBrokerAppGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private LoanSerializer loanSerializer;
    private BankSerializer bankSerializer;

    public LoanBrokerAppGateway() {
        try {
            this.sender = new MessageSenderGateway("loanRequest", "loanRequest");
            this.receiver = new MessageReceiverGateway("loanReply","loanReply");
            this.loanSerializer = new LoanSerializer();
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void applyForLoan(LoanRequest request) {
        try {
            String json = this.loanSerializer.requestToString(request);
            Message message = this.sender.createTextMessage(json);
            message.setJMSReplyTo(receiver.getDestination());
            message.setJMSMessageID(UUID.randomUUID().toString());

            this.sender.send(message);
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
            for (StackTraceElement elem : ex.getStackTrace()) {
                System.out.println(elem.toString());
            }
        }
    }

    public void setLoanReplyListener(LoanReplyListener listener) {
        try {
            this.receiver.setListener(message ->
                    listener.onLoanReplyArrived(null, loanSerializer.replyFromString(message.toString())));
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void setBankRequestListener(BankRequestListener listener) {
        try {
            this.receiver.setListener(message ->
                    listener.onBankRequestArrived(bankSerializer.requestFromString(message.toString())));
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }
}
