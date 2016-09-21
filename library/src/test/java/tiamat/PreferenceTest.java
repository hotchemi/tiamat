package tiamat;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import rx.Subscription;
import rx.functions.Action1;
import rx.observers.TestSubscriber;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@SuppressLint({"NewApi", "CommitPrefEdits"})
public class PreferenceTest {

    static class TestPreference extends RxSharedPreferences {
        TestPreference(SharedPreferences preferences) {
            super(preferences);
        }
    }

    private SharedPreferences preferences;

    private RxSharedPreferences rxPreferences;

    @Before
    public void setUp() {
        preferences = getDefaultSharedPreferences(RuntimeEnvironment.application);
        rxPreferences = new TestPreference(preferences);
    }

    @Test
    public void getDefaultValue() {
        assertThat(rxPreferences.getBoolean("key1", false).asValue()).isFalse();
        assertThat(rxPreferences.getFloat("key2", 0F).asValue()).isZero();
        assertThat(rxPreferences.getInt("key3", 0).asValue()).isZero();
        assertThat(rxPreferences.getLong("key4", 0L).asValue()).isZero();
        assertThat(rxPreferences.getString("key5", null).asValue()).isNull();
        assertThat(rxPreferences.getStringSet("key6", null).asValue()).isNull();
    }

    @Test
    public void getStoredValue() {
        preferences.edit().putBoolean("key1", true).commit();
        assertThat(rxPreferences.getBoolean("key1", false).asValue()).isEqualTo(true);

        preferences.edit().putFloat("key2", 1).commit();
        assertThat(rxPreferences.getFloat("key2", 0).asValue()).isEqualTo(1);

        preferences.edit().putInt("key3", 1).commit();
        assertThat(rxPreferences.getInt("key3", 0).asValue()).isEqualTo(1);

        preferences.edit().putLong("key4", 1).commit();
        assertThat(rxPreferences.getLong("key4", 0).asValue()).isEqualTo(1);

        preferences.edit().putString("key5", "value").commit();
        assertThat(rxPreferences.getString("key5", null).asValue()).isEqualTo("value");

        preferences.edit().putStringSet("key6", singleton("value")).commit();
        assertThat(rxPreferences.getStringSet("key6", null).asValue()).isEqualTo(singleton("value"));
    }

    @Test
    public void putValue() {
        rxPreferences.putBoolean("key1", true);
        assertThat(rxPreferences.getBoolean("key1", false).asValue()).isEqualTo(true);

        rxPreferences.putFloat("key2", 1);
        assertThat(rxPreferences.getFloat("key2", 0).asValue()).isEqualTo(1);

        rxPreferences.putInt("key3", 1);
        assertThat(rxPreferences.getInt("key3", 0).asValue()).isEqualTo(1);

        rxPreferences.putLong("key4", 1);
        assertThat(rxPreferences.getLong("key4", 0).asValue()).isEqualTo(1);

        rxPreferences.putString("key5", "value");
        assertThat(rxPreferences.getString("key5", null).asValue()).isEqualTo("value");

        rxPreferences.putStringSet("key6", singleton("value"));
        assertThat(rxPreferences.getStringSet("key6", null).asValue()).isEqualTo(singleton("value"));
    }

    @Test
    public void containsValue() {
        assertThat(rxPreferences.contains("key1")).isEqualTo(false);
        rxPreferences.putBoolean("key1", true);
        assertThat(rxPreferences.contains("key1")).isEqualTo(true);

        assertThat(rxPreferences.contains("key2")).isEqualTo(false);
        rxPreferences.putFloat("key2", 1);
        assertThat(rxPreferences.contains("key2")).isEqualTo(true);

        assertThat(rxPreferences.contains("key3")).isEqualTo(false);
        rxPreferences.putInt("key3", 1);
        assertThat(rxPreferences.contains("key3")).isEqualTo(true);

        assertThat(rxPreferences.contains("key4")).isEqualTo(false);
        rxPreferences.putLong("key4", 1);
        assertThat(rxPreferences.contains("key4")).isEqualTo(true);

        assertThat(rxPreferences.contains("key5")).isEqualTo(false);
        rxPreferences.putString("key5", "value");
        assertThat(rxPreferences.contains("key5")).isEqualTo(true);

        assertThat(rxPreferences.contains("key6")).isEqualTo(false);
        rxPreferences.putStringSet("key6", singleton("value"));
        assertThat(rxPreferences.contains("key6")).isEqualTo(true);
    }

    @Test
    public void removeValue() {
        rxPreferences.putBoolean("key1", true);
        assertThat(rxPreferences.contains("key1")).isEqualTo(true);
        rxPreferences.remove("key1");
        assertThat(rxPreferences.contains("key1")).isEqualTo(false);

        rxPreferences.putFloat("key2", 1);
        assertThat(rxPreferences.contains("key2")).isEqualTo(true);
        rxPreferences.remove("key2");
        assertThat(rxPreferences.contains("key2")).isEqualTo(false);

        rxPreferences.putInt("key3", 1);
        assertThat(rxPreferences.contains("key3")).isEqualTo(true);
        rxPreferences.remove("key3");
        assertThat(rxPreferences.contains("key3")).isEqualTo(false);

        rxPreferences.putLong("key4", 1);
        assertThat(rxPreferences.contains("key4")).isEqualTo(true);
        rxPreferences.remove("key4");
        assertThat(rxPreferences.contains("key4")).isEqualTo(false);

        rxPreferences.putString("key5", "value");
        assertThat(rxPreferences.contains("key5")).isEqualTo(true);
        rxPreferences.remove("key5");
        assertThat(rxPreferences.contains("key5")).isEqualTo(false);

        rxPreferences.putStringSet("key6", singleton("value"));
        assertThat(rxPreferences.contains("key6")).isEqualTo(true);
        rxPreferences.remove("key6");
        assertThat(rxPreferences.contains("key6")).isEqualTo(false);
    }

    @Test
    public void clearValue() {
        rxPreferences.putBoolean("key1", true);
        rxPreferences.putFloat("key2", 1);
        rxPreferences.putInt("key3", 1);
        rxPreferences.putLong("key4", 1);
        rxPreferences.putString("key5", "value");
        rxPreferences.putStringSet("key6", singleton("value"));
        assertThat(rxPreferences.getAll().size()).isEqualTo(6);
        rxPreferences.clear();
        assertThat(rxPreferences.getAll().size()).isZero();
    }

    @Test
    public void asObservable() {
        Preference<String> preference = rxPreferences.getString("key1", "defValue");

        TestSubscriber<String> o = new TestSubscriber<>();
        Subscription subscription = preference.asObservable().subscribe(o);
        o.assertValues("defValue");

        rxPreferences.putString("key1", "value1");
        o.assertValues("defValue", "value1");

        rxPreferences.remove("key1");
        o.assertValues("defValue", "value1", "defValue");

        subscription.unsubscribe();
        rxPreferences.putString("key1", "foo");
        o.assertValues("defValue", "value1", "defValue");
    }

    @Test
    public void asObservableHonorsBackpressure() {
        Preference<String> preference = rxPreferences.getString("key1", "defValue");

        TestSubscriber<String> o = new TestSubscriber<>(2);
        preference.asObservable().subscribe(o);
        o.assertValues("defValue");

        rxPreferences.putString("key1", "value1");
        o.assertValues("defValue", "value1");

        rxPreferences.putString("key1", "value2");
        o.assertValues("defValue", "value1"); // No new item due to backpressure.

        o.requestMore(1);
        o.assertValues("defValue", "value1", "value2");

        for (int i = 0; i < 1000; i++) {
            rxPreferences.putString("key1", "value" + i);
        }
        o.assertValues("defValue", "value1", "value2"); // No new items due to backpressure.

        o.requestMore(Long.MAX_VALUE);
        o.assertValues("defValue", "value1", "value2", "value999"); // only latest.
    }

    @Test
    public void asAction() {
        Preference<String> preference = rxPreferences.getString("key1", "defValue");
        Action1<? super String> action = preference.asAction();

        action.call("value1");
        assertThat(preferences.getString("key1", null)).isEqualTo("value1");

        action.call("value2");
        assertThat(preferences.getString("key1", null)).isEqualTo("value2");
    }

    @After
    public void cleanup() {
        preferences.edit().clear().commit();
    }
}
