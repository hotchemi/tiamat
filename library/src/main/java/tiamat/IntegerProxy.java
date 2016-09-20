package tiamat;

import android.content.SharedPreferences;

final class IntegerProxy implements Proxy<Integer> {

    static final IntegerProxy INSTANCE = new IntegerProxy();

    @Override
    public Integer get(String key, Integer defValue, SharedPreferences preferences) {
        return preferences.getInt(key, defValue);
    }

    @Override
    public void set(String key, Integer value, SharedPreferences.Editor editor) {
        editor.putInt(key, value);
    }
}
