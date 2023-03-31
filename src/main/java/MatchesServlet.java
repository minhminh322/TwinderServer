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

public class MatchesServlet extends HttpServlet {


    private Gson gson = new Gson();

    private TwinderDao dbDao;
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("Initialize Matches Servlet");
        dbDao = new TwinderDao();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        TwinderDao dbDao = new TwinderDao();
        String urlPath = request.getPathInfo();
        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");
        int userID = 0;
        if (isUrlValid(urlParts)) {
            userID = Integer.parseInt(urlParts[1]);
        }


        try {
            ArrayList<Integer> result = dbDao.getMatches(userID);
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(gson.toJson(result));
            out.flush();

        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getOutputStream().print(gson.toJson("False"));
            response.getOutputStream().flush();
        }

//        // Send the response
//        PrintWriter out = response.getWriter();
//        out.println("<h1>" + "Hello World" + "</h1>");
    }
    private boolean isUrlValid(String[] urlPath) {
        // TODO: validate the request url path according to the API spec
        // urlPath  = "/1/seasons/2019/day/1/skier/123"
        // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
        return true;
    }
}
