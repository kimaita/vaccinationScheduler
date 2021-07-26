package org.kimaita.vaccinationscheduler;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.kimaita.vaccinationscheduler.Constants.PASSWORD;
import static org.kimaita.vaccinationscheduler.Constants.URL;
import static org.kimaita.vaccinationscheduler.Constants.USER;

public class DatabaseUtils {

    private static PreparedStatement st = null;

    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void insertUser(String username, String email, String phone, int natID, int PIN, Connection conn) throws SQLException {
        String ins = "INSERT into Parent(parent_name, phone_number, email, nat_id, user_pin) values(?,?,?,?,?)";
        st = conn.prepareStatement(ins);
        st.setString(1, username);
        st.setString(2, phone);
        st.setString(3, email);
        st.setInt(4, natID);
        st.setInt(5, PIN);
        st.executeUpdate();
        st.close();
    }

    public static ResultSet userDets(int natID) throws SQLException {
        String sel = "SELECT * FROM Parent WHERE nat_id = ? LIMIT 1";
        st = createConnection().prepareStatement(sel);
        st.setInt(1, natID);
        return st.executeQuery();
    }
}
