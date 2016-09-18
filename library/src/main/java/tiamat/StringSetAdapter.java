package tiamat;

import android.content.SharedPreferences;

import java.util.Set;

final class StringSetAdapter implements Adapter<Set<String>> {

    static final StringSetAdapter INSTANCE = new StringSetAdapter();

    @Override
    public Set<String> get(String key, SharedPreferences preferences) {
        return preferences.getStringSet(key, null);
    }

    @Override
    public void set(String key, Set<String> value, SharedPreferences.Editor editor) {
        editor.putStringSet(key, value);
    }
}
