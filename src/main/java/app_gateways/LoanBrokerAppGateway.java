package app_gateways;

import com.rabbitmq.jms.client.message.RMQBytesMessage;
import listeners.BankRequestListener;
import listeners.LoanReplyListener;
import message_gateways.MessageReceiverGateway;
import message_gateways.MessageSenderGateway;
import models.bank.BankInterestReply;
import models.bank.BankInterestRequest;
import models.loan.LoanReply;
import models.loan.LoanRequest;
import serializers.BankSerializer;
import serializers.LoanSerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoanBrokerAppGateway {

    private MessageSenderGateway loanSender;
    private MessageReceiverGateway loanReceiver;
    private LoanSerializer loanSerializer;

    private MessageSenderGateway bankSender;
    private MessageReceiverGateway bankReceiver;
    private BankSerializer bankSerializer;

    private Map<BankInterestRequest, Message> bankRequests = new HashMap<>();
    private Map<String, LoanRequest> loanRequests = new HashMap<>();

    public LoanBrokerAppGateway() {
        try {
            this.loanSender = new MessageSenderGateway("loanRequest", "loanRequest");
            this.loanReceiver = new MessageReceiverGateway("loanReply","loanReply");

            this.bankSender = new MessageSenderGateway("bankReply", "bankReply");
            this.bankReceiver = new MessageReceiverGateway("bankRequest","bankRequest");

            this.loanSerializer = new LoanSerializer();
            this.bankSerializer = new BankSerializer();
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void applyForLoan(LoanRequest request) {
        try {
            String json = this.loanSerializer.requestToString(request);
            Message message = this.loanSender.createTextMessage(json);

            this.loanSender.send(message);

            loanRequests.put(message.getJMSMessageID(), request);
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void sendBankReply(BankInterestRequest request, BankInterestReply reply) {
        try {
            Message requestMessage = bankRequests.get(request);
            String replyJson = this.bankSerializer.replyToString(reply);
            Message replyMessage = this.bankSender.createTextMessage(replyJson);
            replyMessage.setJMSCorrelationID(requestMessage.getJMSMessageID());

            bankSender.send(replyMessage);
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void setLoanReplyListener(LoanReplyListener listener) {
        try {
            this.loanReceiver.setListener(message -> {
                try {
                    RMQBytesMessage bytesMessage = (RMQBytesMessage) message;
                    byte[] buffer = new byte[(int) bytesMessage.getBodyLength()];
                    bytesMessage.readBytes(buffer);

                    LoanReply loanReply = loanSerializer.replyFromString(new String(buffer));

                    listener.onLoanReplyArrived(loanRequests.get(message.getJMSCorrelationID()), loanReply);
                } catch (JMSException ex) {
                    Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
                }
            });
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void setBankRequestListener(BankRequestListener listener) {
        try {
            this.bankReceiver.setListener(message -> {
                try {
                    RMQBytesMessage bytesMessage = (RMQBytesMessage) message;
                    byte[] buffer = new byte[(int) bytesMessage.getBodyLength()];
                    bytesMessage.readBytes(buffer);

                    BankInterestRequest bankRequest = bankSerializer.requestFromString(new String(buffer));
                    bankRequests.put(bankRequest, message);

                    Logger.getAnonymousLogger().log(Level.SEVERE, "Retrieve request " + message.getJMSMessageID());

                    listener.onBankRequestArrived(bankRequest);
                } catch (JMSException ex) {
                    Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
                }
            });
        } catch (JMSException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }
}