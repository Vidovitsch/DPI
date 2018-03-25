package message_gateways;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Session;
import java.util.function.Consumer;

public class MessageSenderGateway {

    private Connection connection;
    private Session session;
    private Destination destination;
    private Consumer consumer;

    public MessageSenderGateway(String channelName) {

    }

    public Message createTextMessge(String body) {
        return null;
    }

    public void send(Message message) {

    }
}
