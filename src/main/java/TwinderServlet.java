import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;


class TwinderBody {
    private String swipe;
    private int swiper;
    private int swipee;
    private String comment;

    private long startTime;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getSwipe() {
        return swipe;
    }

    public void setSwipe(String swipe) {
        this.swipe = swipe;
    }

    public int getSwiper() {
        return swiper;
    }

    public void setSwiper(int swiper) {
        this.swiper = swiper;
    }

    public int getSwipee() {
        return swipee;
    }

    public void setSwipee(int swipee) {
        this.swipee = swipee;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
public class TwinderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set response content type to text
        response.setContentType("text/html");

        // sleep for 1000ms. You can vary this value for different tests
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Send the response
        PrintWriter out = response.getWriter();
        out.println("<h1>" + "Hello World" + "</h1>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();

        try {
//            BufferedReader reader = request.getReader();
            TwinderBody body = gson.fromJson(request.getReader(), TwinderBody.class);

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
}
