package serializers;

import com.google.gson.Gson;
import models.bank.BankInterestReply;
import models.bank.BankInterestRequest;

public class BankSerializer {

    private Gson gson = new Gson();

    public String requestToString(BankInterestRequest request) {
        return gson.toJson(request);
    }

    public BankInterestRequest requestFromString(String string) {
        return gson.fromJson(string, BankInterestRequest.class);
    }

    public String replyToString(BankInterestReply reply) {
        return gson.toJson(reply);
    }

    public BankInterestReply replyFromString(String string) {
        return gson.fromJson(string, BankInterestReply.class);
    }
}
