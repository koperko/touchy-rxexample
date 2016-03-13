package cz.qest.rxkotlinexample;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.jakewharton.rxbinding.view.RxView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

/**
 * Created by koper on 11.03.16.
 */
public class CanvasFragment extends Fragment {


    public CanvasFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_canvas, container, false);

        final Socket mSocket = TouchyApplication.getSocket();


        mSocket.on("bxy", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("DrawingView", args[0].toString());
            }
        });

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        });

        mSocket.on("news", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("DrawingView", args[0].toString());
            }
        });



        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.BEVEL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12f);



        Paint mReceivedPaint = new Paint();
        mReceivedPaint.setAntiAlias(true);
        mReceivedPaint.setDither(true);
        mReceivedPaint.setColor(Color.RED);
        mReceivedPaint.setStyle(Paint.Style.STROKE);
        mReceivedPaint.setStrokeJoin(Paint.Join.BEVEL);
        mReceivedPaint.setStrokeCap(Paint.Cap.ROUND);
        mReceivedPaint.setStrokeWidth(12f);




        DrawingView mDrawingView = new DrawingView(getContext());
        mDrawingView.setPaint(mPaint);
        mDrawingView.setReceivedPaint(mReceivedPaint);
        ((FrameLayout) mView).addView(mDrawingView);


        return mView;
    }
}
