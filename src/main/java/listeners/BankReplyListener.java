package listeners;

import models.bank.BankInterestReply;
import models.bank.BankInterestRequest;

public interface BankReplyListener {

    void onBankReplyArrived(BankInterestRequest request, BankInterestReply reply);
}
