package tiamat;

import android.content.SharedPreferences;

import java.util.Collections;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public final class RxSharedPreferences {

    private static final Float DEFAULT_FLOAT = 0f;
    private static final Integer DEFAULT_INTEGER = 0;
    private static final Boolean DEFAULT_BOOLEAN = false;
    private static final Long DEFAULT_LONG = 0L;

    public static RxSharedPreferences create(SharedPreferences preferences) {
        checkNotNull(preferences, "preferences == null");
        return new RxSharedPreferences(preferences);
    }

    private static void checkNotNull(Object o, String message) {
        if (o == null) {
            throw new NullPointerException(message);
        }
    }

    private final SharedPreferences preferences;
    private final Observable<String> keyChanges;

    private RxSharedPreferences(SharedPreferences preferences) {
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

    public Preference<Boolean> getBoolean(String key) {
        return getBoolean(key, DEFAULT_BOOLEAN);
    }

    public Preference<Boolean> getBoolean(String key, Boolean defaultValue) {
        checkNotNull(key, "key == null");
        return new Preference<>(preferences, key, defaultValue, BooleanAdapter.INSTANCE, keyChanges);
    }

    public Preference<Float> getFloat(String key) {
        return getFloat(key, DEFAULT_FLOAT);
    }

    public Preference<Float> getFloat(String key, Float defaultValue) {
        checkNotNull(key, "key == null");
        return new Preference<>(preferences, key, defaultValue, FloatAdapter.INSTANCE, keyChanges);
    }

    public Preference<Integer> getInteger(String key) {
        return getInteger(key, DEFAULT_INTEGER);
    }

    public Preference<Integer> getInteger(String key, Integer defaultValue) {
        checkNotNull(key, "key == null");
        return new Preference<>(preferences, key, defaultValue, IntegerAdapter.INSTANCE, keyChanges);
    }

    public Preference<Long> getLong(String key) {
        return getLong(key, DEFAULT_LONG);
    }

    public Preference<Long> getLong(String key, Long defaultValue) {
        checkNotNull(key, "key == null");
        return new Preference<>(preferences, key, defaultValue, LongAdapter.INSTANCE, keyChanges);
    }

    public Preference<String> getString(String key) {
        return getString(key, null);
    }

    public Preference<String> getString(String key, String defaultValue) {
        checkNotNull(key, "key == null");
        return new Preference<>(preferences, key, defaultValue, StringAdapter.INSTANCE, keyChanges);
    }

    public Preference<Set<String>> getStringSet(String key) {
        return getStringSet(key, Collections.<String>emptySet());
    }

    public Preference<Set<String>> getStringSet(String key, Set<String> defaultValue) {
        checkNotNull(key, "key == null");
        return new Preference<>(preferences, key, defaultValue, StringSetAdapter.INSTANCE, keyChanges);
    }
}
