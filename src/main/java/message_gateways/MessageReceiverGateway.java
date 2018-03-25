package message_gateways;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.jms.Session;
import java.util.function.Consumer;

public class MessageReceiverGateway {

    private Connection connection;
    private Session session;
    private Destination destination;
    private Consumer consumer;

    public MessageReceiverGateway(String channelName) {

    }

    public void setListener(MessageListener listener) {

    }
}
