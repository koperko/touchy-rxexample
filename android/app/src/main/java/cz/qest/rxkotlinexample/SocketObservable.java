package cz.qest.rxkotlinexample;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by koper on 13.03.16.
 */
public class SocketObservable<T> extends Observable<T> {

    public SocketObservable(final String aEvent){
        super(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> t) {
                TouchyApplication.getSocket().on(aEvent, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {
                            if(!t.isUnsubscribed()){
                                t.onNext((T) args[0]);
                            }
                        } catch (Exception e) {
                            t.onError(e);
                        }
                    }
                });
            }
        });

    }

}
