package cz.qest.rxkotlinexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.github.nkzawa.emitter.Emitter;
import com.jakewharton.rxbinding.view.RxView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by koper on 11.03.16.
 */
public class DrawingView extends View {


    private static final int TOUCH_EVENT_BUFFER_SIZE = 10000;
    private ArrayBlockingQueue<Pair<Float, Float>> fMotionEvents;

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;

    private Paint mPaint;
    private Paint mReceivedPaint;
    private boolean mBreakPathFlag = false;

    private int mUserId;
    private ArrayBlockingQueue<Pair<Float, Float>> fReceivedPoints;
    private Float mReceivedX;
    private Float mReceivedY;
    private Path mReceivedPath;

    public DrawingView(Context c) {
        super(c);
        context = c;
        mPath = new Path();
        mReceivedPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);

        final int[] mCount = {0};

        fMotionEvents = new ArrayBlockingQueue<>(TOUCH_EVENT_BUFFER_SIZE);
        fReceivedPoints = new ArrayBlockingQueue<>(TOUCH_EVENT_BUFFER_SIZE);

        TouchyApplication.getSocket().on("news", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject mEvent = (JSONObject) args[0];
                try {
                    mUserId = mEvent.getInt("user");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        RxView.touches(this)
                .map(new Func1<MotionEvent, MotionEvent>() {
                    @Override
                    public MotionEvent call(MotionEvent t) {
                        MotionEvent e = MotionEvent.obtain(t); // returned objects are mutable .. we need to get ourselves a copy
                        circlePath.reset();
                        circlePath.addCircle(mX, mY, 30, Path.Direction.CW);


                        switch (e.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                fMotionEvents.add(new Pair<>(e.getX(), e.getY()));
                                try {
                                    TouchyApplication.getSocket().emit("xy", new JSONObject().put("x", e.getX()).put("y", e.getY()).put("user", mUserId));
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                                break;
                            case MotionEvent.ACTION_MOVE:
                                fMotionEvents.add(new Pair<>(e.getX(), e.getY()));
                                try {
                                    TouchyApplication.getSocket().emit("xy", new JSONObject().put("x", e.getX()).put("y", e.getY()).put("user", mUserId));
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                                fMotionEvents.add(new Pair<>(-1f, -1f));
                                try {
                                    TouchyApplication.getSocket().emit("xy", new JSONObject().put("x", -1f).put("y", -1f).put("user", mUserId));
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                                break;
                        }

                        postInvalidate();
                        return e;
                    }
                })
                .delay(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MotionEvent>() {
                    @Override
                    public void onCompleted() {
                        Log.d("", "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("", "");
                    }

                    @Override
                    public void onNext(MotionEvent t) {
                        try {
                            fMotionEvents.take();
                            postInvalidate();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                        Log.d("DrawingView", "MotionEvent: x=" + t.getX() + "\ty=" + t.getY());
//                        Log.d("DrawingView", "count = " + ++mCount[0]);
//                        Log.d("DrawingView", "fMotionEventsCount = " + fMotionEvents.size());
                    }
                });

        Observable<JSONObject> mReceivedObservable = new SocketObservable<>("bxy");
//        mReceivedObservable
//                .map(new Func1<JSONObject, JSONObject>() {
//                    @Override
//                    public JSONObject call(JSONObject t) {
//                        float x, y;
//                        try {
//                            x = t.get("x") instanceof Integer ? ((Integer) t.get("x")).floatValue() : ((Double) t.get("x")).floatValue();
//                            y = t.get("y") instanceof Integer ? ((Integer) t.get("y")).floatValue() : ((Double) t.get("y")).floatValue();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            return null;
//                        }
//
//                        fReceivedPoints.add(new Pair<>(x, y));
//                        postInvalidate();
//                        return t;
//                    }
//                })
//                .delay(5, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<JSONObject>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.d("", "");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("SocketObservable", " error occurred: " + e.toString());
//                    }
//
//                    @Override
//                    public void onNext(JSONObject t) {
//                        try {
//                            fReceivedPoints.take();
//                            postInvalidate();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });


        mReceivedObservable
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(JSONObject t) {
                        float x, y;
                        try {
                            x = t.get("x") instanceof Integer ? ((Integer) t.get("x")).floatValue() : ((Double) t.get("x")).floatValue();
                            y = t.get("y") instanceof Integer ? ((Integer) t.get("y")).floatValue() : ((Double) t.get("y")).floatValue();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }

                        fReceivedPoints.add(new Pair<>(x, y));
                        postInvalidate();
                    }
                });


        mReceivedObservable
                .delay(5, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {
                        Log.d("", "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("SocketObservable", " error occurred: " + e.toString());
                    }

                    @Override
                    public void onNext(JSONObject t) {
                        try {
                            fReceivedPoints.take();
                            postInvalidate();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
//        mCanvas.save();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.restore();

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        if (fMotionEvents.iterator().hasNext()) {
            Pair mFirst = fMotionEvents.iterator().next();
            mPath.reset();
            mPath.moveTo((Float) mFirst.first, (Float) mFirst.second);
            mX = (Float) mFirst.first;
            mY = (Float) mFirst.second;

            for (Pair o : fMotionEvents) {
                float x = (Float) o.first;
                float y = (Float) o.second;

                if (x == -1 || y == -1) {
                    mBreakPathFlag = true;
                    continue;
                }

                if (mBreakPathFlag) {
                    mPath.moveTo(x, y);
                    mX = x;
                    mY = y;
                    mBreakPathFlag = false;
                }

                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                    mX = x;
                    mY = y;
                }
            }
        }

        mBreakPathFlag = false;

        if (fReceivedPoints.iterator().hasNext()) {
            Pair mFirst = fReceivedPoints.iterator().next();
            mReceivedPath.reset();
            mReceivedPath.moveTo((Float) mFirst.first, (Float) mFirst.second);
            mReceivedX = (Float) mFirst.first;
            mReceivedY = (Float) mFirst.second;

            for (Pair o : fReceivedPoints) {
                float x = (Float) o.first;
                float y = (Float) o.second;

                if (x == -1 || y == -1) {
                    mBreakPathFlag = true;
                    continue;
                }

                if (mBreakPathFlag) {
                    mReceivedPath.moveTo(x, y);
                    mReceivedX = x;
                    mReceivedY = y;
                    mBreakPathFlag = false;
                }

                float dx = Math.abs(x - mReceivedX);
                float dy = Math.abs(y - mReceivedY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mReceivedPath.quadTo(mReceivedX, mReceivedY, (x + mReceivedX) / 2, (y + mReceivedY) / 2);
                    mReceivedX = x;
                    mReceivedY = y;
                }
            }
        }

        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mReceivedPath, mReceivedPaint);

        canvas.drawPath(circlePath, circlePaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 0;

//    private void touch_start(float x, float y) {
//        mPath.reset();
//        mPath.moveTo(x, y);
//        mX = x;
//        mY = y;
//    }
//
//    private void touch_move(float x, float y) {
//        float dx = Math.abs(x - mX);
//        float dy = Math.abs(y - mY);
//        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
//            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
//            mX = x;
//            mY = y;
//
//
//        }
//    }
//
//    private void touch_up() {
//        mPath.lineTo(mX, mY);
//        circlePath.reset();
//        // commit the path to our offscreen
//        mCanvas.drawPath(mPath, mPaint);
//        // kill this so we don't double draw
//        mPath.reset();
//    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        float x = event.getX();
//        float y = event.getY();
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                touch_start(x, y);
//                invalidate();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                touch_move(x, y);
//                invalidate();
//                break;
//            case MotionEvent.ACTION_UP:
//                touch_up();
//                invalidate();
//                break;
//        }
//        return true;
//    }

    public Paint getPaint() {
        return mPaint;
    }

    public void setPaint(Paint aPaint) {
        mPaint = aPaint;
    }

    public Paint getReceivedPaint() {
        return mReceivedPaint;
    }

    public void setReceivedPaint(Paint aMReceivedPaint) {
        mReceivedPaint = aMReceivedPaint;
    }
}
