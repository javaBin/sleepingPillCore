package no.java.sleepingpill.core.attachment;

import no.java.sleepingpill.core.Configuration;
import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.database.Postgres;
import no.java.sleepingpill.core.exceptions.InternalError;
import no.java.sleepingpill.core.util.IdGenerator;
import org.jsonbuddy.JsonFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AttachmentService {
    public static AttachmentService get() {
        return new AttachmentService();
    }


    private static final ConcurrentMap<String,byte[]> dummyStore = new ConcurrentHashMap<>();

    private static String INSERT_SQL = "insert into ATTACHMENT(id,content) values (?,?)";

    public ServiceResult addPicture(byte[] content) {
        String id = IdGenerator.newId();
        if (!Configuration.persistToDb()) {
            dummyStore.put(id,content);
            return ServiceResult.ok(JsonFactory.jsonObject().put("id",id));

        }
        try (Connection connection = Postgres.openConnection(); PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1,id);
            statement.setBytes(2,content);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ServiceResult.ok(JsonFactory.jsonObject().put("id",id));

    }

    public Optional<byte[]> getPicture(String id) {
        return Optional.ofNullable(dummyStore.get(id));

    }
}
