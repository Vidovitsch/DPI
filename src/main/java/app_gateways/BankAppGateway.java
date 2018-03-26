package app_gateways;

import com.rabbitmq.jms.client.message.RMQBytesMessage;
import listeners.BankReplyListener;
import message_gateways.MessageReceiverGateway;
import message_gateways.MessageSenderGateway;
import models.bank.BankInterestReply;
import models.bank.BankInterestRequest;
import serializers.BankSerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BankAppGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private BankSerializer serializer;

    private Map<String, BankInterestRequest> bankRequests = new HashMap<>();

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

            this.sender.send(message);

            bankRequests.put(message.getJMSMessageID(), request);
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void setBankReplyListener(BankReplyListener listener) {
        try {
            this.receiver.setListener(message -> {
                try {
                    BankInterestReply bankReply = serializer.replyFromBytesMessage((RMQBytesMessage) message);
                    BankInterestRequest bankRequest = bankRequests.get(message.getJMSCorrelationID());

                    listener.onBankReplyArrived(bankRequest, bankReply);
                } catch (JMSException ex) {
                    Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
                }
            });

        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }
}
