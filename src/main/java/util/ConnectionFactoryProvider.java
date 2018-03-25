package util;

import com.rabbitmq.jms.admin.RMQConnectionFactory;

import javax.jms.ConnectionFactory;

public class ConnectionFactoryProvider {

    private static ConnectionFactory connectionFactory = null;

    public static ConnectionFactory getInstance() {
        if (connectionFactory == null) {
            connectionFactory = createConnectionFactory();
        }
        return connectionFactory;
    }

    private static ConnectionFactory createConnectionFactory() {
        RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
        connectionFactory.setHost("192.168.24.77");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        return connectionFactory;
    }
}
