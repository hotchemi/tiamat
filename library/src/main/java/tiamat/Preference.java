package tiamat;

import android.content.SharedPreferences;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class Preference<T> {

    private final SharedPreferences preferences;
    private final String key;
    private final T defValue;
    private final Proxy<T> proxy;
    private final Observable<T> values;

    Preference(SharedPreferences preferences, final String key, T defValue, Proxy<T> proxy, Observable<String> keyChanges) {
        this.preferences = preferences;
        this.key = key;
        this.defValue = defValue;
        this.proxy = proxy;
        this.values = keyChanges
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String changedKey) {
                        return key.equals(changedKey);
                    }
                })
                .startWith("<init>") // Dummy value to trigger initial load.
                .onBackpressureLatest()
                .map(new Func1<String, T>() {
                    @Override
                    public T call(String ignored) {
                        return asValue();
                    }
                });
    }

    public void set(T value) {
        SharedPreferences.Editor editor = preferences.edit();
        proxy.set(key, value, editor);
        editor.apply();
    }

    public T asValue() {
        return proxy.get(key, defValue, preferences);
    }

    public Observable<T> asObservable() {
        return values;
    }

    public Action1<? super T> asAction() {
        return new Action1<T>() {
            @Override
            public void call(T value) {
                set(value);
            }
        };
    }
}
