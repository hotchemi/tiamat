package tiamat;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;


public abstract class RxSharedPreferences {

    private final SharedPreferences preferences;
    private final Observable<String> changeEvents;

    protected RxSharedPreferences(Context context, String name) {
        this(context.getSharedPreferences(name, Context.MODE_PRIVATE));
    }

    protected RxSharedPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
        this.changeEvents = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> observableEmitter) throws Exception {
                final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
                        observableEmitter.onNext(key);
                    }
                };

                RxSharedPreferences.this.preferences.registerOnSharedPreferenceChangeListener(listener);

                observableEmitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        RxSharedPreferences.this.preferences.unregisterOnSharedPreferenceChangeListener(listener);
                    }
                });
            }
        }).share();
    }

    protected Preference<Boolean> getBoolean(String key, boolean defValue) {
        return new Preference<>(preferences, key, defValue, BooleanProxy.INSTANCE, changeEvents);
    }

    protected Preference<Float> getFloat(String key, float defValue) {
        return new Preference<>(preferences, key, defValue, FloatProxy.INSTANCE, changeEvents);
    }

    protected Preference<Integer> getInt(String key, int defValue) {
        return new Preference<>(preferences, key, defValue, IntegerProxy.INSTANCE, changeEvents);
    }

    protected Preference<Long> getLong(String key, long defValue) {
        return new Preference<>(preferences, key, defValue, LongProxy.INSTANCE, changeEvents);
    }

    protected Preference<String> getString(String key, String defValue) {
        return new Preference<>(preferences, key, defValue, StringProxy.INSTANCE, changeEvents);
    }

    protected Preference<Set<String>> getStringSet(String key, Set<String> defValue) {
        return new Preference<>(preferences, key, defValue, StringSetProxy.INSTANCE, changeEvents);
    }

    protected void putBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    protected void putString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    protected void putFloat(String key, float value) {
        preferences.edit().putFloat(key, value).apply();
    }

    protected void putInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    protected void putLong(String key, long value) {
        preferences.edit().putLong(key, value).apply();
    }

    protected void putStringSet(String key, Set<String> value) {
        preferences.edit().putStringSet(key, value).apply();
    }

    protected boolean contains(String key) {
        return preferences.contains(key);
    }

    protected void remove(String key) {
        preferences.edit().remove(key).apply();
    }

    public Map<String, ?> getAll() {
        return preferences.getAll();
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}
