package tiamat;

import android.content.SharedPreferences;

final class BooleanProxy implements Proxy<Boolean> {

    static final BooleanProxy INSTANCE = new BooleanProxy();

    @Override
    public Boolean get(String key, Boolean defValue, SharedPreferences preferences) {
        return preferences.getBoolean(key, defValue);
    }

    @Override
    public void set(String key, Boolean value, SharedPreferences.Editor editor) {
        editor.putBoolean(key, value);
    }
}
