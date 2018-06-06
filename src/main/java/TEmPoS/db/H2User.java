package TEmPoS.db;

import TEmPoS.Util.Password;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class H2User extends H2Base {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(H2User.class);


    public H2User(ConnectionSupplier connectionSupplier) {
        super(connectionSupplier.provide());
        try {
            initTable(getConnection());
        } catch (Exception e) {
            LOG.error("Can't find database driver: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void initTable(Connection conn) throws SQLException {
        execute(conn, "CREATE TABLE IF NOT EXISTS users (id int AUTO_INCREMENT, username VARCHAR(255) PRIMARY KEY, hash VARCHAR(255), isAdmin VARCHAR(32))");
    }

    public synchronized boolean login(@NonNull final String userName, @NonNull final String password) {
        errIfClosed();
        try {
            return loginSQL(userName, password);
        } catch (SQLException e) {
            LOG.error("Can't login " + userName + ": " + e.getMessage());
            return false;
        }
    }

    public synchronized boolean register(@NonNull final String userName, @NonNull final String password, @NonNull final String isAdmin) {
        errIfClosed();
        try {
            return registerSQL(userName, password, isAdmin);
        } catch (SQLException e) {
            LOG.error("Can't register " + userName + ": " + e.getMessage());
            return false;
        }
    }

    public void deleteUser(String username) {
        final String DELETE_USER_QUERY = "DELETE FROM users WHERE username=?";
        try (PreparedStatement ps = getConnection().prepareStatement(DELETE_USER_QUERY)) {
            ps.setString(1, username);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean editUser(String targetUser, String username, String isAdmin) {
        final String EDIT_USER_QUERY = "UPDATE users SET username =?, isAdmin=? WHERE username=?";
        try (PreparedStatement ps = getConnection().prepareStatement(EDIT_USER_QUERY)) {
            ps.setString(1, username);
            ps.setString(2, isAdmin);
            ps.setString(3, targetUser);
            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

        public boolean isRegistered(String userName) {
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT 1 FROM users WHERE username = ?")) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    public String getUserDetails(String username){
        final String GET_USER_QUERY = "SELECT id, username, isAdmin FROM users WHERE username=?";
        String details = null;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_USER_QUERY)){
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                details = rs.getString(1) + " / " +  rs.getString(2) + " / " +  rs.getString(3);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return details;
    }

    public JSONObject getUsers(){
        final String GET_USER_QUERY = "SELECT id, username, isAdmin FROM users";
        JSONObject userList = new JSONObject();
        try (PreparedStatement ps = getConnection().prepareStatement(GET_USER_QUERY)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String id = rs.getString(1);
                String username = rs.getString(2);
                String isAdmin = rs.getString(3);
                Map<String, String> user = new LinkedHashMap<>();
                user.put("username" , username);
                user.put("isAdmin" , isAdmin);
                userList.put(id , new JSONObject(user));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

        public boolean isAdmin(String userName) {
        final String IS_ADMIN_QUERY = "SELECT 1 FROM users WHERE (username=? AND isAdmin=?)";
        try (PreparedStatement ps = getConnection().prepareStatement(IS_ADMIN_QUERY)){
            ps.setString(1, userName);
            ps.setString(2, "Y");
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }

    private boolean loginSQL(String userName, String password) throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT hash FROM users WHERE username = ?")) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("hash");
                return hash != null && validate(password, hash);
            }
        }
        return false;
    }

    private boolean registerSQL(String userName, String password, String isAdmin) throws SQLException {
        String hash = hash(password);
        if (hash == null) {
            return false;
        }
        if (hasUserSQL(userName)) {
            return false;
        }
        String query = "INSERT into users (username, hash, isAdmin) VALUES(?,?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(query)) {
            ps.setString(1, userName);
            ps.setString(2, hash);
            ps.setString(3, isAdmin);
            int count = ps.executeUpdate();
            LOG.debug("insert count = " + count);
            return count == 1;
        }
    }


    private boolean hasUserSQL(String userName) throws SQLException {
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT username FROM users WHERE username = ?")) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private boolean validate(String password, String hash) {
        try {
            return Password.validatePassword(password, hash);
        } catch (Password.PasswordException e) {
            LOG.error("Can't validate password: " + e.getMessage());
            return false;
        }
    }

    private String hash(String password) {
        try {
            return Password.createHash(password);
        } catch (Password.PasswordException e) {
            LOG.error("Can't hash password <" + password + ">: " + e.getMessage());
            return null;
        }
    }



}
