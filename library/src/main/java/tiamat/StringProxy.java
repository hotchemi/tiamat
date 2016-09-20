package tiamat;

import android.content.SharedPreferences;

final class StringProxy implements Proxy<String> {

    static final StringProxy INSTANCE = new StringProxy();

    @Override
    public String get(String key, String defValue, SharedPreferences preferences) {
        return preferences.getString(key, defValue);
    }

    @Override
    public void set(String key, String value, SharedPreferences.Editor editor) {
        editor.putString(key, value);
    }
}
