package serializers;

import com.google.gson.Gson;
import models.bank.BankInterestReply;
import models.bank.BankInterestRequest;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

public class BankSerializer {

    private Gson gson = new Gson();

    public String requestToString(BankInterestRequest request) {
        return gson.toJson(request);
    }

    public BankInterestRequest requestFromBytesMessage(BytesMessage message) throws JMSException {
        byte[] buffer = new byte[(int) message.getBodyLength()];
        message.readBytes(buffer);

        return requestFromString(new String(buffer));
    }

    public String replyToString(BankInterestReply reply) {
        return gson.toJson(reply);
    }

    public BankInterestReply replyFromBytesMessage(BytesMessage message) throws JMSException {
        byte[] buffer = new byte[(int) message.getBodyLength()];
        message.readBytes(buffer);

        return replyFromString(new String(buffer));
    }

    private BankInterestRequest requestFromString(String string) {
        return gson.fromJson(string, BankInterestRequest.class);
    }

    private BankInterestReply replyFromString(String string) {
        return gson.fromJson(string, BankInterestReply.class);
    }
}
