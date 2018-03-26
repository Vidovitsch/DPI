package app_gateways;

import com.rabbitmq.jms.client.message.RMQBytesMessage;
import listeners.LoanRequestListener;
import message_gateways.MessageReceiverGateway;
import message_gateways.MessageSenderGateway;
import models.loan.LoanReply;
import models.loan.LoanRequest;
import serializers.LoanSerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoanClientAppGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private LoanSerializer serializer;

    private Map<LoanRequest, Message> loanRequests = new HashMap<>();

    public LoanClientAppGateway() {
        try {
            this.sender = new MessageSenderGateway("loanReply", "loanReply");
            this.receiver = new MessageReceiverGateway("loanRequest", "loanRequest");
            this.serializer = new LoanSerializer();
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void sendLoanReply(LoanRequest request, LoanReply reply) {
        try {
            Message requestMessage = loanRequests.get(request);
            String json = this.serializer.replyToString(reply);
            Message replyMessage = this.sender.createTextMessage(json);
            replyMessage.setJMSCorrelationID(requestMessage.getJMSMessageID());

            this.sender.send(replyMessage);
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void setLoanRequestListener(LoanRequestListener listener) {
        try {
            this.receiver.setListener(message -> {
                try {
                    LoanRequest request = serializer.requestFromMessage((RMQBytesMessage) message);
                    listener.onLoanRequestArrived(request);

                    loanRequests.put(request, message);
                } catch (Exception ex) {
                    Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
                }
            });
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }
}
