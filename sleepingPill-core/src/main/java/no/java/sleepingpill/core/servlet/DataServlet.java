package no.java.sleepingpill.core.servlet;

import no.java.sleepingpill.core.ServiceLocator;
import no.java.sleepingpill.core.ServiceResult;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Optional;
import java.util.UUID;

public class DataServlet extends HttpServlet {
    private ComputePath computePath = new ComputePath();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        Optional<ServletOperation> operationOptional = computePath.computePost(pathInfo);
        if (!operationOptional.isPresent()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Unknown path " + pathInfo);
            return;
        }
        JsonObject payload = JsonParser.parseToObject(req.getReader());
        try (ServiceLocator ignored = ServiceLocator.startTransaction()) {
            ServiceResult serviceResult;
            switch (operationOptional.get()) {

                default:
                    throw new UnsupportedOperationException("Unknown operation " + operationOptional.get());
            }
            //if (serviceResult.getResult().isPresent()) {
            //    serviceResult.getResult().get().toJson(resp.getWriter());
            //}
        }
    }

    private void testAdd(HttpServletRequest req, HttpServletResponse resp) {
        String value = Configuration.myLocation() + "test/" + UUID.randomUUID().toString();
        System.out.println("Returned: " + value);
        resp.addHeader("location", value);
    }

    private String eventidFromAddTalk(String pathInfo) {
        pathInfo.indexOf("/","/event/".length());
        return pathInfo.substring("/event/".length(),pathInfo.indexOf("/","/event/".length()));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String res = toString(getClass().getClassLoader().getResourceAsStream("examples/allyear.json"));
        resp.getWriter().append(res);
    }

    private static String toString(InputStream inputStream) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            StringBuilder result = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                result.append((char)c);
            }
            return result.toString();
        }
    }
}
