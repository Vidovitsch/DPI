package app_gateways;

import listeners.BankReplyListener;
import message_gateways.MessageReceiverGateway;
import message_gateways.MessageSenderGateway;
import models.bank.BankInterestReply;
import models.bank.BankInterestRequest;
import serializers.BankSerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BankAppGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private BankSerializer serializer;

    public BankAppGateway() {
        try {
            this.sender = new MessageSenderGateway("bankRequest", "bankRequest");
            this.receiver = new MessageReceiverGateway("bankReply", "bankReply");
            this.serializer = new BankSerializer();
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void sendBankRequest(BankInterestRequest request) {
        try {
            String json = this.serializer.requestToString(request);
            Message message = this.sender.createTextMessage(json);
            message.setJMSReplyTo(receiver.getDestination());
            message.setJMSMessageID(UUID.randomUUID().toString());

            this.sender.send(message);
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void setBankReplyListener(BankReplyListener listener) {
        try {
            this.receiver.setListener(message ->
                    listener.onBankReplyArrived(null, serializer.replyFromString(message.toString())));
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }
}
