package tiamat;

import android.content.SharedPreferences;

final class LongProxy implements Proxy<Long> {

    static final LongProxy INSTANCE = new LongProxy();

    @Override
    public Long get(String key, Long defValue, SharedPreferences preferences) {
        return preferences.getLong(key, defValue);
    }

    @Override
    public void set(String key, Long value, SharedPreferences.Editor editor) {
        editor.putLong(key, value);
    }
}
