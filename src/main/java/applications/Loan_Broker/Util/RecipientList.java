package applications.Loan_Broker.Util;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecipientList {

    private Map<String, String> recipients = new HashMap<>();
    private Evaluator evaluator = new Evaluator();

    public void addRecipient(String recipientName, String rule) {
        recipients.put(recipientName, rule);
    }

    public List<String> evaluateRules(int amount, int time) {
        List<String> evaluatedRecipients = new ArrayList<>();
        try {
            evaluator.putVariable("amount", String.valueOf(amount));
            evaluator.putVariable("time", String.valueOf(time));

            for (Entry<String, String> entry : recipients.entrySet()) {
                if (evaluator.evaluate(entry.getValue()).equals("1.0")) {
                    evaluatedRecipients.add(entry.getKey());
                }
            }
        } catch (EvaluationException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }

        return evaluatedRecipients;
    }
}
