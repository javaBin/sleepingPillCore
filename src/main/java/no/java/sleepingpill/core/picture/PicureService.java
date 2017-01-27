package no.java.sleepingpill.core.picture;

import no.java.sleepingpill.core.Configuration;
import no.java.sleepingpill.core.ServiceResult;
import no.java.sleepingpill.core.database.Postgres;
import no.java.sleepingpill.core.util.IdGenerator;
import org.jsonbuddy.JsonFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private static String FETCH_SQL = "select content,contenttype from  PICTURE where id = ?";

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
        if (id == null) {
            return Optional.empty();
        }
        if (!Configuration.persistToDb()) {
            return Optional.ofNullable(dummyStore.get(id));
        }
        try (Connection connection = Postgres.openConnection(); PreparedStatement statement = connection.prepareStatement(FETCH_SQL)) {
            statement.setString(1,id);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                byte[] content = rs.getBytes(1);
                String contenttype = rs.getString(2);
                return Optional.of(new Picture(content,contenttype));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
