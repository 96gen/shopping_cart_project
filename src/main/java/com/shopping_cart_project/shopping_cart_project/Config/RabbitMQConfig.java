package com.shopping_cart_project.shopping_cart_project.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "emailQueue";
    public static final String EXCHANGE_NAME = "emailExchange";

    @Bean
    //RabbitMQ產生持久化的Queue，可以依序完成要求的功能（送Email）
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    //RabbitMQ Exchange，接收Producer的要求，送到對應的Queue中
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    //將Queue綁定到指定的Exchange，當routingKey相同時，才會分到Queue中
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("email.routing.key");
    }
}
