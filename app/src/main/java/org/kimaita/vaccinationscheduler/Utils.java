package org.kimaita.vaccinationscheduler;

import org.kimaita.vaccinationscheduler.models.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Utils {

    public static Single<Boolean> isInternetAvailable() {

        return Single.fromCallable(() -> {
            try {
                // Connect to Google DNS to check for connection
                int timeoutMs = 1500;
                Socket socket = new Socket();
                InetSocketAddress socketAddress = new InetSocketAddress("8.8.8.8", 53);

                socket.connect(socketAddress, timeoutMs);
                socket.close();

                return true;
            } catch (IOException e) {
                return false;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static User readUserFile(File file) {
        User usr = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            usr = (User) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usr;
    }

    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy H:mm a");
}
