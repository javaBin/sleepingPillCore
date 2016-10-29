package no.java.sleepingpill.core.util;

import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.exceptions.ServiceResultException;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.pojo.JsonGenerator;
import spark.ResponseTransformer;

public class JsonUtil {

    public static String resolveToString(Object object) {

        ServiceResult serviceResult = (ServiceResult) object;
        if (serviceResult.getResult().isPresent()) {
            return serviceResult.getResult().get().toJson();
        } else {
            throw new ServiceResultException(serviceResult);
        }

    }

    public static ResponseTransformer jsonBuddyString() {
        return JsonUtil::resolveToString;
    }

}
