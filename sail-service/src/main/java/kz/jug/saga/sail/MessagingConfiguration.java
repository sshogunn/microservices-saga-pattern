package kz.jug.saga.sail;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfiguration {
    private final String brokerUrl;

    public MessagingConfiguration(@Value("${spring.activemq.broker-url}")String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(this.brokerUrl);
        return activeMQConnectionFactory;
    }
}
