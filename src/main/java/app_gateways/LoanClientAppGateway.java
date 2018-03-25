package app_gateways;

import message_gateways.MessageReceiverGateway;
import message_gateways.MessageSenderGateway;
import models.loan.LoanReply;
import models.loan.LoanRequest;
import serializers.LoanSerializer;

public class LoanClientAppGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private LoanSerializer serializer;

    public void onLoanRequestArrived(LoanRequest request) {

    }

    public void sendLoanReply(LoanRequest request, LoanReply reply) {

    }
}
