package util;

import com.rabbitmq.jms.admin.RMQConnectionFactory;

import javax.jms.ConnectionFactory;

public class ConnectionFactoryProvider {

    private static ConnectionFactory jmsConnectionFactory = null;
    private static com.rabbitmq.client.ConnectionFactory rmqConnectionFactory = null;

    public static ConnectionFactory getJMSConnectionFactory() {
        if (jmsConnectionFactory == null) {
            jmsConnectionFactory = createJMSConnectionFactory();
        }
        return jmsConnectionFactory;
    }

    public static com.rabbitmq.client.ConnectionFactory getRMQConnectionFactory() {
        if (rmqConnectionFactory == null) {
            rmqConnectionFactory = createRMQConnectionFactory();
        }
        return rmqConnectionFactory;
    }

    private static ConnectionFactory createJMSConnectionFactory() {
        RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
        connectionFactory.setHost("192.168.24.77");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        return connectionFactory;
    }

    private static com.rabbitmq.client.ConnectionFactory createRMQConnectionFactory() {
        com.rabbitmq.client.ConnectionFactory connectionFactory = new com.rabbitmq.client.ConnectionFactory();
        connectionFactory.setHost("192.168.24.77");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        return connectionFactory;
    }
}
