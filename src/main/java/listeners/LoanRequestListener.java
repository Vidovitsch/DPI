package listeners;

import models.loan.LoanRequest;

public interface LoanRequestListener {

    void onLoanRequestArrived(LoanRequest request);
}
