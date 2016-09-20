package tiamat;

import android.content.SharedPreferences;

final class FloatAdapter implements Adapter<Float> {

    static final FloatAdapter INSTANCE = new FloatAdapter();

    @Override
    public Float get(String key, Float defValue, SharedPreferences preferences) {
        return preferences.getFloat(key, defValue);
    }

    @Override
    public void set(String key, Float value, SharedPreferences.Editor editor) {
        editor.putFloat(key, value);
    }
}
