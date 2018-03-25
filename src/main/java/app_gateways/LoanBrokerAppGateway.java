package app_gateways;

import message_gateways.MessageReceiverGateway;
import message_gateways.MessageSenderGateway;
import models.loan.LoanReply;
import models.loan.LoanRequest;
import serializers.LoanSerializer;

public class LoanBrokerAppGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private LoanSerializer serializer;

    public void applyForLoan(LoanRequest request) {

    }

    public void onLoanReplyArrived(LoanRequest request, LoanReply reply) {

    }
}
