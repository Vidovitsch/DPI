package message_gateways;

import com.rabbitmq.jms.client.message.RMQBytesMessage;
import com.rabbitmq.jms.client.message.RMQTextMessage;
import util.ConnectionFactoryProvider;
import util.DestinationProvider;

import javax.jms.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageSenderGateway {

    private Session session;
    private MessageProducer producer;
    private Destination sendDestination;

    public MessageSenderGateway(String destinationName, String routingKey) throws JMSException {
        try {
            Connection connection = ConnectionFactoryProvider.getJMSConnectionFactory().createConnection();
            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.sendDestination = DestinationProvider.getInstance(destinationName, routingKey);
            this.producer = this.session.createProducer(sendDestination);
        } catch (IOException | TimeoutException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    public Destination getDestination() {
        return this.sendDestination;
    }

    public Message createTextMessage(String body) throws JMSException {
        return this.session.createTextMessage(body);
    }

    public void send(Message message) throws JMSException {
        this.producer.send(message);
    }

    public void send(Message message, Destination destination) throws JMSException {
        this.producer.send(destination, message);
    }
}
