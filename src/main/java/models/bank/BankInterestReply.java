package models.bank;

/**
 * This class stores information about the bank reply
 *  to a loan request of the specific client
 * 
 */
public class BankInterestReply {

    private double interest; // the loan interest
    private String bankId; // the unique quote Id

    public BankInterestReply(double interest, String quoteId) {
        this.interest = interest;
        this.bankId = quoteId;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String toString() {
        return "quote=" + this.bankId + " interest=" + this.interest;
    }
}
