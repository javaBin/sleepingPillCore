package no.java.sleepingpill.core.servlet;

import no.java.sleepingpill.core.ServiceLocator;
import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.session.SessionService;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;

import static org.eclipse.jetty.http.HttpMethod.GET;
import static org.eclipse.jetty.http.HttpMethod.POST;
import static org.eclipse.jetty.http.HttpMethod.PUT;

public class DataServlet extends HttpServlet {
    private ComputePath computePath = new ComputePath();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        Optional<ServletOperation> operationOptional = computePath.findOperation(pathInfo, POST);
        if (!operationOptional.isPresent()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown path " + pathInfo);
            return;
        }
        JsonObject payload = JsonParser.parseToObject(req.getReader());
        try (ServiceLocator ignored = ServiceLocator.startTransaction()) {
            ServiceResult serviceResult;
            switch (operationOptional.get()) {
                case SESSION_IN_EVENT:
                    serviceResult = SessionService.instance().addSession(pathInfo.substring(1, pathInfo.indexOf("/", 1)), payload);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown operation " + operationOptional.get());
            }
            handleResult(resp, serviceResult);
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        Optional<ServletOperation> operationOptional = computePath.findOperation(pathInfo, PUT);
        if (!operationOptional.isPresent()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown path " + pathInfo);
            return;
        }
        JsonObject payload = JsonParser.parseToObject(req.getReader());
        try (ServiceLocator ignored = ServiceLocator.startTransaction()) {
            ServiceResult serviceResult;
            switch (operationOptional.get()) {
                case SESSION_BY_ID:
                    String sessionid = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
                    serviceResult = SessionService.instance().updateSession(sessionid, payload);
                    break;
                case ALL_EVENTS:
                    serviceResult = SessionService.instance().allArrangedEvents();
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown operation " + operationOptional.get());
            }
            handleResult(resp, serviceResult);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        Optional<ServletOperation> operationOptional = computePath.findOperation(pathInfo, GET);
        if (!operationOptional.isPresent()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown path " + pathInfo);
            return;
        }
        try (ServiceLocator ignored = ServiceLocator.startTransaction()) {
            ServiceResult serviceResult;
            switch (operationOptional.get()) {
                case SESSION_BY_ID:
                    serviceResult = SessionService.instance().sessionById(pathInfo.substring(pathInfo.lastIndexOf("/") + 1));
                    break;
                case ALL_EVENTS:
                    serviceResult = SessionService.instance().allArrangedEvents();
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown operation " + operationOptional.get());
            }
            handleResult(resp, serviceResult);
        }
    }

    private void handleResult(HttpServletResponse resp, ServiceResult serviceResult) throws IOException {
        if (serviceResult.getResult().isPresent()) {
            serviceResult.getResult().get().toJson(resp.getWriter());
        } else {
            serviceResult.sendError(resp);
        }
    }

    private static String toString(InputStream inputStream) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            StringBuilder result = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                result.append((char) c);
            }
            return result.toString();
        }
    }
}
