package loanbroker;

public class Consumer {

    private LoanBrokerFrame loanBrokerFrame;

    private static Consumer instance = null;

    private Consumer(LoanBrokerFrame loanBrokerFrame) {
        this.loanBrokerFrame = loanBrokerFrame;
    }

    public static Consumer getInstance(LoanBrokerFrame loanBrokerFrame) {
        if (instance == null) {
            instance = new Consumer(loanBrokerFrame);
        }
        return instance;
    }

    public String consume() {
        MessageL
    }
}
