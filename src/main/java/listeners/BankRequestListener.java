package listeners;

import models.bank.BankInterestRequest;

public interface BankRequestListener {

    void onBankRequestArrived(BankInterestRequest request);
}
