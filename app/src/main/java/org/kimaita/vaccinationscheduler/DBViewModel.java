package org.kimaita.vaccinationscheduler;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.kimaita.vaccinationscheduler.models.Child;
import org.kimaita.vaccinationscheduler.models.Hospital;
import org.kimaita.vaccinationscheduler.models.Message;
import org.kimaita.vaccinationscheduler.models.Vaccine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.kimaita.vaccinationscheduler.DatabaseUtils.insertMessage;
import static org.kimaita.vaccinationscheduler.DatabaseUtils.selectChatMessages;
import static org.kimaita.vaccinationscheduler.DatabaseUtils.selectChildren;
import static org.kimaita.vaccinationscheduler.DatabaseUtils.selectHospitalDetails;
import static org.kimaita.vaccinationscheduler.DatabaseUtils.selectHospitals;
import static org.kimaita.vaccinationscheduler.DatabaseUtils.selectUserMessages;
import static org.kimaita.vaccinationscheduler.DatabaseUtils.selectVaccines;

public class DBViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Child>> mChildren;
    private MutableLiveData<ArrayList<Vaccine>> mVaccines;
    private MutableLiveData<ArrayList<Hospital>> mHospitals;
    private MutableLiveData<Hospital> mHospitalDets;
    private MutableLiveData<Boolean> messageSent;
    private MutableLiveData<ArrayList<Message>> chatMessages;
    private MutableLiveData<ArrayList<Message>> chatList;

    public MutableLiveData<ArrayList<Child>> getChildren(int id) {
        if (mChildren == null) {
            mChildren = new MutableLiveData<>();
            loadChildren(id);
        }
        return mChildren;
    }

    public MutableLiveData<ArrayList<Vaccine>> getVaccines() {
        if (mVaccines == null) {
            mVaccines = new MutableLiveData<>();
            loadVaccines();
        }
        return mVaccines;
    }

    public MutableLiveData<ArrayList<Hospital>> getHospitals() {
        if (mHospitals == null) {
            mHospitals = new MutableLiveData<>();
            loadHospitals();
        }
        return mHospitals;
    }

    public MutableLiveData<ArrayList<Message>> getChatList(int uID) {
        if (chatList == null) {
            chatList = new MutableLiveData<>();
            loadChatList(uID);
        }
        return chatList;
    }

    public MutableLiveData<ArrayList<Message>> getChatMessages(int uID, int hosID) {
        if (chatMessages == null) {
            chatMessages = new MutableLiveData<>();
            loadChatMessages(uID, hosID);
        }
        return chatMessages;
    }

    public MutableLiveData<Hospital> getmHospital(int hosID) {
        if (mHospitalDets == null) {
            mHospitalDets = new MutableLiveData<>();
            loadHospitalDets(hosID);
        }
        return mHospitalDets;
    }

    public LiveData<Boolean> sendMessage(Message message) {
        if (messageSent == null) {
            messageSent = new MutableLiveData<>();
            new SendMessageAsyncTask().execute(message);
        }
        return messageSent;
    }

    private void loadHospitals() {
        new FetchHospitals().execute();
    }

    private void loadHospitalDets(int hosID) {
        new FetchHospitalDetails().execute(hosID);
    }

    private void loadVaccines() {
        new FetchVaccines().execute();
    }

    private void loadChildren(int id) {
        new FetchChildren().execute(id);
    }

    private void loadChatList(int uID) {
        new FetchMessageList().execute(uID);
    }

    private void loadChatMessages(int hosID, int UID) {
        new FetchChatMessages().execute(hosID, UID);
    }

    class FetchChildren extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            int id = integers[0];
            ArrayList<Child> children = new ArrayList<>();
            try {
                ResultSet rs = selectChildren(id);
                while (rs.next()) {
                    Child child = new Child();
                    child.setChildDBID(rs.getInt("child_id"));
                    child.setChildName(rs.getString("child_name"));
                    child.setChildDoB(rs.getDate("dob"));
                    Log.i("Fetching User Children", "Fetched Successfully: " + child.getChildName());
                    children.add(child);

                }
                rs.close();
                mChildren.postValue(children);

            } catch (SQLException s) {
                Log.e("Fetching User Children", "Error Occurred.", s);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }

    class FetchVaccines extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<Vaccine> vaccines = new ArrayList<>();
            try {
                ResultSet rs = selectVaccines();
                while (rs.next()) {
                    Vaccine vaccine = new Vaccine();
                    vaccine.setVaccineName(rs.getString("name"));
                    vaccine.setVaccineAdministration(rs.getString("administration"));
                    vaccines.add(vaccine);
                }
                rs.close();
                mVaccines.postValue(vaccines);
            } catch (SQLException s) {
                Log.e("Fetching Vaccine List", "Error Occurred.", s);
            }
            return null;
        }
    }

    class FetchHospitals extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<Hospital> hospitals = new ArrayList<>();
            try {
                ResultSet rs = selectHospitals();
                while (rs.next()) {
                    Hospital hos = new Hospital();
                    hos.setHospital_id(rs.getInt("hospital_id"));
                    hos.setHospital_name(rs.getString("hospital_name"));
                    hospitals.add(hos);
                }
                rs.close();
                mHospitals.postValue(hospitals);
            } catch (SQLException s) {
                Log.e("Fetching Hospital List", "Error Occurred.", s);
            }
            return null;
        }
    }

    class SendMessageAsyncTask extends AsyncTask<Message, Void, Void> {
        @Override
        protected Void doInBackground(Message... messages) {
            boolean success = false;
            Message message = messages[0];
            try {
                insertMessage(message.getSender(), message.getHospital(), message.getContent(), message.getTime(), message.isRead());
                success = true;
            } catch (SQLException s) {
                Log.e("Sending Message", "Error Occurred.", s);
            }
            messageSent.postValue(success);
            return null;
        }

    }

    class FetchMessageList extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            int userID = integers[0];
            ArrayList<Message> messages = new ArrayList<>();
            try {
                ResultSet rs = selectUserMessages(userID);
                while (rs.next()) {
                    Message message = new Message();
                    //content, time, read_status, hospital, hospital_name
                    message.setContent(rs.getString("content"));

                    message.setHospital(rs.getInt("hospital"));
                    message.setRead(rs.getBoolean("read_status"));
                    message.setTime(rs.getTimestamp("time").getTime());
                    message.setHospitalName(rs.getString("hospital_name"));
                    Log.i("Fetching Chat List", "Fetched Successfully: " + message.getContent());
                    messages.add(message);
                }
                rs.close();
                chatList.postValue(messages);
            } catch (SQLException s) {
                Log.e("Fetching Chat List", "Error Occurred.", s);
            }
            return null;
        }
    }

    class FetchChatMessages extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            int user = integers[0];
            int hospital = integers[1];
            ArrayList<Message> mMessages = new ArrayList<>();
            try {
                ResultSet rs = selectChatMessages(user, hospital);
                while (rs.next()) {
                    Message message = new Message();
                    message.setId(rs.getInt("id"));
                    message.setParent(rs.getInt("parent"));
                    message.setHospital(rs.getInt("hospital"));
                    message.setSender(rs.getInt("sender"));
                    message.setContent(rs.getString("content"));
                    message.setTime(rs.getTimestamp("time").getTime());
                    message.setRead(rs.getBoolean("read_status"));
                    mMessages.add(message);
                }
                rs.close();
                chatMessages.postValue(mMessages);
            } catch (SQLException s) {
                Log.e("Fetching Chat Messages", "Error Occurred.", s);
            }
            return null;
        }
    }

    class FetchHospitalDetails extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            int hosID = integers[0];
            try {
                ResultSet rs = selectHospitalDetails(hosID);
                Hospital hospital = new Hospital();
                while (rs.next()) {

                    hospital.setHospital_id(hosID);
                    hospital.setHospital_name(rs.getString("hospital_name"));
                    hospital.setEmail_address(rs.getString("email_address"));
                    hospital.setLatitude(rs.getDouble("longitude"));
                    hospital.setLongitude(rs.getDouble("latitude"));
                }
                rs.close();
                mHospitalDets.postValue(hospital);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }
    }
}
