package tiamat;

import android.content.SharedPreferences;

final class BooleanAdapter implements Adapter<Boolean> {

    static final BooleanAdapter INSTANCE = new BooleanAdapter();

    @Override
    public Boolean get(String key, SharedPreferences preferences) {
        return preferences.getBoolean(key, false);
    }

    @Override
    public void set(String key, Boolean value, SharedPreferences.Editor editor) {
        editor.putBoolean(key, value);
    }
}
