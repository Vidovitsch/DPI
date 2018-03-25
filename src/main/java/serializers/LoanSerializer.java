package serializers;

import com.google.gson.Gson;
import models.loan.LoanReply;
import models.loan.LoanRequest;

public class LoanSerializer {

    private Gson gson = new Gson();

    public String requestToString(LoanRequest request) {
        return gson.toJson(request);
    }

    public LoanRequest requestFromString(String string) {
        return gson.fromJson(string, LoanRequest.class);
    }

    public String replyToString(LoanReply reply) {
        return gson.toJson(reply);
    }

    public LoanReply replyFromString(String string) {
        return gson.fromJson(string, LoanReply.class);
    }
}
