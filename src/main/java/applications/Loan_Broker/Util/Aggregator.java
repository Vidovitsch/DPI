package applications.Loan_Broker.Util;


import models.bank.BankInterestReply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Aggregator {

    private Map<String, Integer> requestAggregations = new HashMap<>();
    private Map<String, List<BankInterestReply>> replyAggregations = new HashMap<>();

    public void registerAggregations(String aggregationId, int numberOfRequests) {
        requestAggregations.put(aggregationId, numberOfRequests);
    }

    public synchronized void addReply(String aggregationId, BankInterestReply reply) {
        if (replyAggregations.containsKey(aggregationId)) {
            replyAggregations.get(aggregationId).add(reply);
        } else {
            List<BankInterestReply> replies = new ArrayList<>();
            replies.add(reply);
            replyAggregations.put(aggregationId, replies);
        }
    }

    public synchronized boolean validateAggregations(String aggregationId) {
        return requestAggregations.get(aggregationId) == replyAggregations.get(aggregationId).size();
    }

    public BankInterestReply getBestReply(String aggregationId) {
        BankInterestReply bestReply = null;
        for (BankInterestReply reply : replyAggregations.get(aggregationId)) {
            if (bestReply == null || reply.getInterest() < bestReply.getInterest()) {
                bestReply = reply;
            }
        }
        return bestReply;
    }
}
