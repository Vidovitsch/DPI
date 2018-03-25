package app_gateways;

import message_gateways.MessageReceiverGateway;
import message_gateways.MessageSenderGateway;
import models.bank.BankInterestReply;
import models.bank.BankInterestRequest;
import serializers.BankSerializer;

public class BankAppGateway {

    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private BankSerializer serializer;

    public void onBankReplyArrived(BankInterestReply reply, BankInterestRequest request) {

    }

    public void sendBankRequest(BankInterestRequest request) {

    }
}
