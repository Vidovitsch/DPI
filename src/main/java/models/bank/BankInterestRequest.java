package models.bank;

import correlation.Correlatable;

/**
 *
 * This class stores all information about an request from a bank to offer
 * a loan to a specific client.
 */
public class BankInterestRequest implements Correlatable {

    private int amount; // the requested loan amount
    private int time; // the requested loan period

    private String correlationId;

    public BankInterestRequest(int amount, int time) {
        super();
        this.amount = amount;
        this.time = time;
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
        return " amount=" + amount + " time=" + time;
    }
}
