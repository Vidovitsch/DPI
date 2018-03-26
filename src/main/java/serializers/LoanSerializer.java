package serializers;

import com.google.gson.Gson;
import models.loan.LoanReply;
import models.loan.LoanRequest;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

public class LoanSerializer {

    private Gson gson = new Gson();

    public String requestToString(LoanRequest request) {
        return gson.toJson(request);
    }

    public LoanRequest requestFromMessage(BytesMessage message) throws JMSException {
        byte[] buffer = new byte[(int) message.getBodyLength()];
        message.readBytes(buffer);

        return requestFromString(new String(buffer));
    }

    public String replyToString(LoanReply reply) {
        return gson.toJson(reply);
    }

    public LoanReply replyFromMessage(BytesMessage message) throws JMSException {
        byte[] buffer = new byte[(int) message.getBodyLength()];
        message.readBytes(buffer);

        return replyFromString(new String(buffer));
    }

    private LoanRequest requestFromString(String string) {
        return gson.fromJson(string, LoanRequest.class);
    }

    private LoanReply replyFromString(String string) {
        return gson.fromJson(string, LoanReply.class);
    }
}
