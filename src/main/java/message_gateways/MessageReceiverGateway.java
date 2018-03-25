package message_gateways;

import util.ConnectionFactoryProvider;
import util.DestinationProvider;

import javax.jms.*;

public class MessageReceiverGateway {

    private Connection connection;
    private Session session;
    private Destination receiveDestination;
    private MessageConsumer consumer;

    public MessageReceiverGateway(String destinationName) throws JMSException {
        this.connection = ConnectionFactoryProvider.getInstance().createConnection();
        this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        this.receiveDestination = DestinationProvider.getInstance(destinationName, destinationName);
        this.consumer = this.session.createConsumer(this.receiveDestination);

        this.connection.start();
    }

    public void setListener(MessageListener listener) throws JMSException {
        this.consumer.setMessageListener(listener);
    }
}
