package app_gateways;

import message_gateways.MessageReceiverGateway;
import message_gateways.MessageSenderGateway;
import models.bank.BankInterestReply;
import models.bank.BankInterestRequest;
import serializers.BankSerializer;
import serializers.LoanSerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BankAppGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private BankSerializer serializer;

    private BankAppGateway() {
        try {
            this.sender = new MessageSenderGateway("bankRequest");
            this.receiver = new MessageReceiverGateway("bankReply");
            this.serializer = new BankSerializer();

            this.setListener();
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void sendBankRequest(BankInterestRequest request) {
        try {
            String json = this.serializer.requestToString(request);
            Message message = this.sender.createTextMessge(json);
            this.sender.send(message);
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public abstract void onBankReplyArrived(BankInterestRequest request, BankInterestReply reply);

    private void setListener() {
        try {
            this.receiver.setListener(message ->
                    onBankReplyArrived(null, serializer.replyFromString(message.toString())));
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }
}
