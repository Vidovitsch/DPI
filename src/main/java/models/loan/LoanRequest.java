package models.loan;

import correlation.Correlatable;

/**
 *
 * This class stores all information about a
 * request that a client submits to get a loan.
 *
 */
public class LoanRequest implements Correlatable {

    private int ssn; // unique client number.
    private int amount; // the amount to borrow
    private int time; // the time-span of the loan

    private String correlationId;

    public LoanRequest(int ssn, int amount, int time) {
        super();
        this.ssn = ssn;
        this.amount = amount;
        this.time = time;
    }

    public int getSsn() {
        return ssn;
    }

    public void setSsn(int ssn) {
        this.ssn = ssn;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String getCorrelationId() {
        return correlationId;
    }

    @Override
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    @Override
    public String toString() {
        return "ssn=" + String.valueOf(ssn) + " amount=" + String.valueOf(amount) + " time=" + String.valueOf(time);
    }
}
