package tiamat;

import android.content.SharedPreferences;

import java.util.Set;

final class StringSetProxy implements Proxy<Set<String>> {

    static final StringSetProxy INSTANCE = new StringSetProxy();

    @Override
    public Set<String> get(String key, Set<String> defValue, SharedPreferences preferences) {
        return preferences.getStringSet(key, defValue);
    }

    @Override
    public void set(String key, Set<String> value, SharedPreferences.Editor editor) {
        editor.putStringSet(key, value);
    }
}
