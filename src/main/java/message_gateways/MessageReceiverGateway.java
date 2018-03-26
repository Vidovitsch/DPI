package message_gateways;

import util.ConnectionFactoryProvider;
import util.DestinationProvider;

import javax.jms.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageReceiverGateway {

    private MessageConsumer consumer;
    private Destination receiveDestination;

    public MessageReceiverGateway(String destinationName, String routingKey) throws JMSException {
        try {
            Connection connection = ConnectionFactoryProvider.getJMSConnectionFactory().createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.receiveDestination = DestinationProvider.getInstance(destinationName, routingKey);
            this.consumer = session.createConsumer(receiveDestination);

            connection.start();
        } catch (IOException | TimeoutException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public void setListener(MessageListener listener) throws JMSException {
        this.consumer.setMessageListener(listener);
    }
}
