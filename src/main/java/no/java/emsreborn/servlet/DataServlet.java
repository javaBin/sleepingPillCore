package no.java.emsreborn.servlet;

import no.java.emsreborn.EventService;
import no.java.emsreborn.ServiceLocator;
import no.java.emsreborn.ServiceResult;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

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
        JsonObject payload = JsonParser.parseToObject(req.getInputStream());
        try (ServiceLocator ignored = ServiceLocator.startTransaction()) {
            ServiceResult serviceResult;
            switch (operationOptional.get()) {
                case ADD_EVENT:
                    serviceResult = EventService.get().addEvent(payload);
                    break;
                case ADD_TALK:
                    serviceResult = EventService.get().addTalk(payload,eventidFromAddTalk(pathInfo));
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown operation " + operationOptional.get());
            }
            serviceResult.getResult().get().toJson(resp.getWriter());
        }
    }

    private String eventidFromAddTalk(String pathInfo) {
        pathInfo.indexOf("/","/event/".length())
        return pathInfo.substring("/event/".length(),pathInfo.indexOf("/","/event/".length()));
    }
}
