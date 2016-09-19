package tiamat;

import android.content.SharedPreferences;

interface Adapter<T> {

    T get(String key, SharedPreferences preferences);

    void set(String key, T value, SharedPreferences.Editor editor);
}