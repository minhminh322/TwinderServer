import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.*;

//import com.google.gson.Gson;
import com.google.gson.Gson;
import com.rabbitmq.client.*;

public class TwinderServlet extends HttpServlet implements Configuration {
    private RMQChannelPool pool;

    private Gson gson = new Gson();

//    private final String[] RPC_QUEUE_LIST = {"LIKE_QUEUE", "MATCH_QUEUE"};
    private static final String[] RPC_QUEUE_LIST = {"TWINDER_QUEUE"};
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("Initialize Servlet");

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername("admin");
            factory.setPassword("admin");
            factory.setVirtualHost("cherry_broker");
            factory.setHost(RabbitMQ_Instance);
            factory.setPort(5672);

            Connection connection = factory.newConnection();
            pool = new RMQChannelPool(500, new RMQChannelFactory(connection));
//            producers = Executors.newFixedThreadPool(100);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String urlPath = request.getPathInfo();
        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");
        String swipeDirection = "";
        if (isUrlValid(urlParts)) {
            swipeDirection = urlParts[1];
        }

        try {
            TwinderBody body = gson.fromJson(request.getReader(), TwinderBody.class);
            body.setSwipe(swipeDirection.toUpperCase());


            for (int i = 0; i < RPC_QUEUE_LIST.length; i++) {
                producer(RPC_QUEUE_LIST[i], gson.toJson(body));
            }

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().print(gson.toJson(body));
            response.getOutputStream().flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getOutputStream().print(gson.toJson("False"));
            response.getOutputStream().flush();
        }
    }

    private void producer(String RPC_QUEUE_NAME, String message) throws InterruptedException {
        Runnable runnable = () -> {
            String corrId = UUID.randomUUID().toString();

            try {
                Channel channel = pool.borrowObject();

                String replyQueueName = channel.queueDeclare().getQueue();
                AMQP.BasicProperties props = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(corrId)
                        .replyTo(replyQueueName)
                        .build();

                channel.basicPublish("", RPC_QUEUE_NAME, props, message.getBytes("UTF-8"));


                final CompletableFuture<String> responseRPC = new CompletableFuture<>();
                String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
                    if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                        responseRPC.complete(new String(delivery.getBody(), "UTF-8"));
                    }
                }, consumerTag -> {
                });
                String result = responseRPC.get();
//                System.out.println(" [.] Got '" + result + "'" + " From " + RPC_QUEUE_NAME);
                channel.basicCancel(ctag);
                pool.returnObject(channel);

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        Thread thr = new Thread(runnable);
        thr.start();
        thr.join();
    }
    private boolean isUrlValid(String[] urlPath) {
        // TODO: validate the request url path according to the API spec
        // urlPath  = "/1/seasons/2019/day/1/skier/123"
        // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
        return true;
    }
}
