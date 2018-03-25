package message_gateways;

import util.ConnectionFactoryProvider;
import util.DestinationProvider;

import javax.jms.*;

public class MessageSenderGateway {

    private Connection connection;
    private Session session;
    private Destination sendDestination;
    private MessageProducer producer;

    public MessageSenderGateway(String destinationName) throws JMSException {
        this.connection = ConnectionFactoryProvider.getInstance().createConnection();
        this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        this.sendDestination = DestinationProvider.getInstance(destinationName, destinationName);
        this.producer = this.session.createProducer(this.sendDestination);
    }

    public Message createTextMessge(String body) throws JMSException {
        return this.session.createTextMessage(body);
    }

    public void send(Message message) throws JMSException {
        this.producer.send(message);
    }
}
