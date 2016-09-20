package tiamat;

import android.content.SharedPreferences;

interface Proxy<T> {

    T get(String key, T defValue, SharedPreferences preferences);

    void set(String key, T value, SharedPreferences.Editor editor);
}
