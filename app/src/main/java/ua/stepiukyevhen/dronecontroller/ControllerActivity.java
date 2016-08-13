package ua.stepiukyevhen.dronecontroller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import ua.stepiukyevhen.dronecontroller.view.ControllerView;

/**
 * @author : Yevhen Stepiuk
 */

public class ControllerActivity extends AppCompatActivity implements Runnable {

    private static final String HOST = "192.168.1.109";
    private static final int PORT = 4444;

    private ControllerView controllerView;
    private Socket transmitterSocket;
    private ObjectOutputStream transmitterOutStream;

    private int[] typr = new int[] {1001, 1500, 1500, 1500};

    public void rewriteTYPR(int[] newTYPR) {
        for (int i = 0; i < typr.length; i++) {
            typr[i] = newTYPR[i];
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        controllerView = new ControllerView(this);
        setContentView(controllerView);

//        AsyncTask.execute(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        controllerView.onResume();
    }

    @Override
    protected void onPause() {
//        try {
//            transmitterOutStream.close();
//            transmitterSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        controllerView.onPause();

        super.onPause();
    }

    @Override
    public void run() {
        try {
            transmitterSocket = new Socket(HOST, PORT);
            transmitterOutStream = new ObjectOutputStream(transmitterSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                transmitterOutStream.writeObject(controllerView.getTYPR());
                transmitterOutStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
