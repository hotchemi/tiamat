package tiamat;

import android.content.SharedPreferences;

/**
 * Stores and retrieves instances of {@code T} in {@link SharedPreferences}.
 */
interface Adapter<T> {

    /**
     * Retrieve the value for {@code key} from {@code preferences}.
     */
    T get(String key, SharedPreferences preferences);

    /**
     * Store non-null {@code value} for {@code key} in {@code editor}.
     * <p>
     * Note: Implementations <b>must not</b> call {@code commit()} or {@code apply()} on
     * {@code editor}.
     */
    void set(String key, T value, SharedPreferences.Editor editor);
}
