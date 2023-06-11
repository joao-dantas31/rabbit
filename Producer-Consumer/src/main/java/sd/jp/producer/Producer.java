package sd.jp.producer;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Producer {
    private final static String QUEUE_NAME = "queue2";

    public static void main(String[] args) throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
        ConnectionFactory factory = new ConnectionFactory();

        try (Connection connection = factory.newConnection(new Address[]{new Address("localhost", 8081), new Address("localhost", 8084), new Address("localhost", 8087)});
             Channel channel = connection.createChannel()) {

            Map<String, Object> arguments  = new HashMap<>();
            //arguments.put("x-queue-type", "quorum");

            channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
            while(true) {
                String message = dtf.format(LocalDateTime.now());

                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            }
        }

    }
}