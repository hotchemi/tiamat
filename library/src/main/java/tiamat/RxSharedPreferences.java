package tiamat;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public abstract class RxSharedPreferences {

    private final SharedPreferences preferences;
    private final Observable<String> keyChanges;

    protected RxSharedPreferences(Context context, String name) {
        this(context.getSharedPreferences(name, Context.MODE_PRIVATE));
    }

    protected RxSharedPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
        this.keyChanges = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
                        subscriber.onNext(key);
                    }
                };

                RxSharedPreferences.this.preferences.registerOnSharedPreferenceChangeListener(listener);

                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        RxSharedPreferences.this.preferences.unregisterOnSharedPreferenceChangeListener(listener);
                    }
                }));
            }
        }).share();
    }

    protected Preference<Boolean> getBoolean(String key) {
        return getBoolean(key, false);
    }

    protected Preference<Boolean> getBoolean(String key, Boolean defaultValue) {
        return new Preference<>(preferences, key, defaultValue, BooleanProxy.INSTANCE, keyChanges);
    }

    protected Preference<Float> getFloat(String key) {
        return getFloat(key, 0f);
    }

    protected Preference<Float> getFloat(String key, Float defaultValue) {
        return new Preference<>(preferences, key, defaultValue, FloatProxy.INSTANCE, keyChanges);
    }

    protected Preference<Integer> getInteger(String key) {
        return getInteger(key, 0);
    }

    protected Preference<Integer> getInteger(String key, Integer defaultValue) {
        return new Preference<>(preferences, key, defaultValue, IntegerProxy.INSTANCE, keyChanges);
    }

    protected Preference<Long> getLong(String key) {
        return getLong(key, 0L);
    }

    protected Preference<Long> getLong(String key, Long defaultValue) {
        return new Preference<>(preferences, key, defaultValue, LongProxy.INSTANCE, keyChanges);
    }

    protected Preference<String> getString(String key) {
        return getString(key, null);
    }

    public Preference<String> getString(String key, String defaultValue) {
        return new Preference<>(preferences, key, defaultValue, StringProxy.INSTANCE, keyChanges);
    }

    protected Preference<Set<String>> getStringSet(String key) {
        return getStringSet(key, Collections.<String>emptySet());
    }

    protected Preference<Set<String>> getStringSet(String key, Set<String> defaultValue) {
        return new Preference<>(preferences, key, defaultValue, StringSetProxy.INSTANCE, keyChanges);
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

    public void clear() {
        preferences.edit().clear().apply();
    }
}
