package sd.jp.producer;

import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Consumer {

    private final static String QUEUE_NAME = "queue2";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection(new Address[]{new Address("localhost", 8081), new Address("localhost", 8084), new Address("localhost", 8087)});
             Channel channel = connection.createChannel()) {

            Map<String, Object> arguments  = new HashMap<>();
            //arguments.put("x-queue-type", "quorum");

            channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            };

            while(true) {
                channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
            }
        }
    }

}
