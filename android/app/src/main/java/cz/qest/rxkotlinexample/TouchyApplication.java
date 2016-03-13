package cz.qest.rxkotlinexample;

import android.app.Application;
import android.provider.SyncStateContract;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by koper on 12.03.16.
 */
public class TouchyApplication extends Application {

    private static Socket fSocket;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            fSocket = IO.socket("http://192.168.10.117:3001");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        fSocket.connect();
    }

    public static Socket getSocket() {
        if(!fSocket.connected()){
            fSocket.connect();
        }
        return fSocket;
    }
}
