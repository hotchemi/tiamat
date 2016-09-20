package tiamat;

import android.content.SharedPreferences;

final class FloatProxy implements Proxy<Float> {

    static final FloatProxy INSTANCE = new FloatProxy();

    @Override
    public Float get(String key, Float defValue, SharedPreferences preferences) {
        return preferences.getFloat(key, defValue);
    }

    @Override
    public void set(String key, Float value, SharedPreferences.Editor editor) {
        editor.putFloat(key, value);
    }
}
