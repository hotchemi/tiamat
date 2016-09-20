package tiamat;

import android.content.SharedPreferences;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

final class Preference<T> {

    private final SharedPreferences preferences;
    private final String key;
    private final T defValue;
    private final Adapter<T> adapter;
    private final Observable<T> values;

    Preference(SharedPreferences preferences, final String key, T defValue, Adapter<T> adapter, Observable<String> keyChanges) {
        this.preferences = preferences;
        this.key = key;
        this.defValue = defValue;
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
                        return asValue();
                    }
                });
    }

    public String key() {
        return key;
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

    public T asValue() {
        return adapter.get(key, defValue, preferences);
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
