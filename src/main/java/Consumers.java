import com.google.gson.Gson;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Consumers {
//    private static ;

    private static final String[] RPC_QUEUE_LIST = {"LIKE_QUEUE", "MATCH_QUEUE"};

    private static Connection connection;

    private static  Gson gson;

    private static MessageConsumer mc;
    private static String processData(String mess) {
        return mess;
    }

    private static void consumer(String RPC_QUEUE_NAME) {
        Runnable runnable = () -> {
            try {
                Channel channel = connection.createChannel();

                channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
                channel.queuePurge(RPC_QUEUE_NAME);

                channel.basicQos(1);

                System.out.println(" [x] Awaiting RPC requests");
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                            .Builder()
                            .correlationId(delivery.getProperties().getCorrelationId())
                            .build();

                    String response = "";
                    try {
                        String message = new String(delivery.getBody(), "UTF-8");
                        TwinderBody tb = gson.fromJson(message, TwinderBody.class);

                        mc.readData(tb);
                        if (RPC_QUEUE_NAME == "LIKE_QUEUE") {
                            response += "LIKE_QUEUE: ";
                            response += String.valueOf(mc.getLikeAndDislike(String.valueOf(tb.getSwiper())));
                        } else if (RPC_QUEUE_NAME == "MATCH_QUEUE") {
                            response += "MATCH_QUEUE: ";
                            response = String.valueOf(mc.getMatch(String.valueOf(tb.getSwiper())));
                        }

//                        System.out.println(" [.] Consume (" + numberOfLike + ")");
//                        response += processData(String.valueOf(numberOfLike));

                    } catch (RuntimeException e) {
                        System.out.println(" [.] " + e);
                    } finally {
                        channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    }
                };

                channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> {}));
            } catch (IOException ex) {
                Logger.getLogger(Consumers.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        for (int i = 0; i < 250; i++) {
            Thread thr = new Thread(runnable);
            thr.start();
        }
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("cherry_broker");
        factory.setHost("34.220.100.97");
        factory.setPort(5672);
        connection = factory.newConnection();
        gson = new Gson();
        mc = new MessageConsumer();


//        MessageConsumer mc = new MessageConsumer();
        for (int i = 0; i < RPC_QUEUE_LIST.length; i++) {
            consumer(RPC_QUEUE_LIST[i]);
        }



    }

}
