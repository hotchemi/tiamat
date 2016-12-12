package tiamat;

import android.content.SharedPreferences;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


public class Preference<T> {

    private final SharedPreferences preferences;
    private final String key;
    private final T defValue;
    private final Proxy<T> proxy;
    private final Observable<T> values;

    Preference(SharedPreferences preferences, final String key, T defValue, Proxy<T> proxy, final Observable<String> keyChanges) {
        this.preferences = preferences;
        this.key = key;
        this.defValue = defValue;
        this.proxy = proxy;
        this.values = keyChanges
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return key.equals(keyChanges);
                    }
                })
                .startWith("") // to trigger initial load
                .map(new Function<String, T>() {
                    @Override
                    public T apply(String s) throws Exception {
                        return asValue();
                    }
                });
    }

    public T asValue() {
        return proxy.get(key, defValue, preferences);
    }

    public Observable<T> asObservable() {
        return values;
    }

    public Consumer<? super T> asConsumer(){
        return new Consumer<T>(){
            @Override
            public void accept(T value) throws Exception {
                SharedPreferences.Editor editor = preferences.edit();
                proxy.set(key, value, editor);
                editor.apply();
            }
        };
    }
}
