package util;

import com.rabbitmq.client.ConnectionFactory;

public class ConnectionFactoryProvider {

    private static ConnectionFactory connectionFactory = null;

    public static ConnectionFactory getInstance() {
        if (connectionFactory == null) {
            connectionFactory = createConnectionFactory();
        }
        return connectionFactory;
    }

    private static ConnectionFactory createConnectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.24.77");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        return connectionFactory;
    }
}
