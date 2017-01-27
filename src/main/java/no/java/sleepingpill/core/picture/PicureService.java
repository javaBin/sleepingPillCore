package no.java.sleepingpill.core.picture;

import no.java.sleepingpill.core.Configuration;
import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.database.Postgres;
import no.java.sleepingpill.core.util.IdGenerator;
import org.jsonbuddy.JsonFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PicureService {
    public static PicureService get() {
        return new PicureService();
    }


    private static final ConcurrentMap<String,Picture> dummyStore = new ConcurrentHashMap<>();

    private static String INSERT_SQL = "insert into PICTURE(id,content,contenttype) values (?,?,?)";

    public ServiceResult addPicture(Picture picture) {
        String id = IdGenerator.newId();
        if (!Configuration.persistToDb()) {
            dummyStore.put(id,picture);
            return ServiceResult.ok(JsonFactory.jsonObject().put("id",id));

        }
        try (Connection connection = Postgres.openConnection(); PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1,id);
            statement.setBytes(2,picture.content);
            statement.setString(3,picture.contenttype);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ServiceResult.ok(JsonFactory.jsonObject().put("id",id));

    }

    public Optional<Picture> getPicture(String id) {
        return Optional.ofNullable(dummyStore.get(id));

    }
}
