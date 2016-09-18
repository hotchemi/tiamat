package tiamat;

import android.content.SharedPreferences;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

final class Preference<T> {

    private final SharedPreferences preferences;
    private final String key;
    //    private final T defaultValue;
    private final Adapter<T> adapter;
    private final Observable<T> values;

    Preference(SharedPreferences preferences, final String key, T defaultValue, Adapter<T> adapter, Observable<String> keyChanges) {
        this.preferences = preferences;
        this.key = key;
//        this.defaultValue = defaultValue;
        this.adapter = adapter;
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
                        return get();
                    }
                });
    }

    public String key() {
        return key;
    }

//    public T defaultValue() {
//        return defaultValue;
//    }

    public T get() {
//        if (!preferences.contains(key)) {
//            return defaultValue;
//        }
        return adapter.get(key, preferences);
    }

    public void set(T value) {
        SharedPreferences.Editor editor = preferences.edit();
        if (value == null) {
            editor.remove(key);
        } else {
            adapter.set(key, value, editor);
        }
        editor.apply();
    }

    public void delete() {
        set(null);
    }

    public Observable<T> asObservable() {
        return values;
    }

    public Observable<T> asSingle() {
        return values.single();
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
