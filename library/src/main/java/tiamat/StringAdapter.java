package tiamat;

import android.content.SharedPreferences;

final class StringAdapter implements Adapter<String> {

    static final StringAdapter INSTANCE = new StringAdapter();

    @Override
    public String get(String key, String defValue, SharedPreferences preferences) {
        return preferences.getString(key, defValue);
    }

    @Override
    public void set(String key, String value, SharedPreferences.Editor editor) {
        editor.putString(key, value);
    }
}
