package org.kimaita.vaccinationscheduler;

import static org.kimaita.vaccinationscheduler.Constants.PASSWORD;
import static org.kimaita.vaccinationscheduler.Constants.URL;
import static org.kimaita.vaccinationscheduler.Constants.USER;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public static void insertChild(String name, Date dob, int parentID, Connection conn) throws SQLException {
        String ins = "INSERT into child(child_name, dob, parent) VALUES(?,?,?)";
        st = conn.prepareStatement(ins);
        st.setString(1, name);
        st.setDate(2, dob);
        st.setInt(3, parentID);
        st.executeUpdate();
        st.close();
    }

    public static ResultSet selectVaccines() throws SQLException {
        String sel = "SELECT name, administration FROM vaccine";
        st = createConnection().prepareStatement(sel);
        return st.executeQuery();
    }

    public static ResultSet selectChildren(int parentID) throws SQLException {
        String sel = "SELECT * FROM child WHERE parent = ? ORDER BY child_id ASC";
        st = createConnection().prepareStatement(sel);
        st.setInt(1, parentID);
        return st.executeQuery();
    }

    public static ResultSet selectHospitals() throws SQLException {
        String sel = "SELECT hospital_name, hospital_id FROM hospital";
        st = createConnection().prepareStatement(sel);
        return st.executeQuery();
    }

    public static ResultSet selectHospitalDetails(int id) throws SQLException {
        String sel = "SELECT hospital_name, hospital_id, email_address, longitude, latitude FROM hospital WHERE hospital_id = ?";
        st = createConnection().prepareStatement(sel);
        st.setInt(1, id);
        return st.executeQuery();
    }

    public static void insertMessage(int parent, String sender, int hospital, String content, long time, boolean read) throws SQLException {
        String ins = "INSERT INTO messages(parent, hospital, sender, content, time, read_status) VALUES (?,?,?,?,?,?)";
        st = createConnection().prepareStatement(ins);
        st.setInt(1, parent);
        st.setInt(2, hospital);
        st.setString(3, sender);
        st.setString(4, content);
        st.setTimestamp(5, new java.sql.Timestamp(time));
        st.setBoolean(6, read);
        st.executeUpdate();
        st.close();
    }

    public static ResultSet selectUserMessages(int id) throws SQLException {
        String sel = "SELECT t1.*, hospital.hospital_name FROM messages t1 INNER JOIN hospital ON t1.hospital = hospital.hospital_id JOIN (SELECT hospital, MAX(time) time FROM messages GROUP BY hospital) t2 ON t1.hospital = t2.hospital AND t1.time = t2.time WHERE t1.parent = ? ORDER BY time DESC";
        st = createConnection().prepareStatement(sel);
        st.setInt(1, id);
        return st.executeQuery();
    }

    public static ResultSet selectChatMessages(int userID, int hosID) throws SQLException {
        String sel = "SELECT * FROM messages WHERE parent = ? AND hospital = ? ORDER BY time ASC";
        st = createConnection().prepareStatement(sel);
        st.setInt(1, userID);
        st.setInt(2, hosID);
        return st.executeQuery();
    }

    public static ResultSet selectSchedule(int childID) throws SQLException {
        String sel = "SELECT item_id, child_id, vaccine_date, vaccine, vaccine.name, administered FROM child_schedule " +
                "INNER JOIN vaccine ON vaccine = vaccine.vaccine_id WHERE child_id = ? ORDER BY `vaccine_date` ASC; ";
        st = createConnection().prepareStatement(sel);
        st.setInt(1, childID);
        return st.executeQuery();
    }
}
