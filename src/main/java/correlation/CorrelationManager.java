package correlation;

import java.util.UUID;

public class CorrelationManager {

    public static void setUUID(Correlatable c) {
        c.setCorrelationId(UUID.randomUUID().toString());
    }

    public static void correlate(Correlatable c1, Correlatable c2) {
        if (c1 != null && c2 != null) {
            if (c1.getCorrelationId() != null && !c1.getCorrelationId().equals("")) {
                c2.setCorrelationId(c1.getCorrelationId());
            } else if (c2.getCorrelationId() != null && c2.getCorrelationId().equals("")) {
                c1.setCorrelationId(c2.getCorrelationId());
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            throw new NullPointerException();
        }
    }
}
