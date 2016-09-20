package tiamat.sample;

import java.util.Set;

import tiamat.Key;
import tiamat.Pref;

@Pref("ExamplePrefs")
public class ExamplePrefs {
    @Key(name = "user_id")
    long userId;
    @Key(name = "user_name")
    String userName;
    @Key(name = "user_age")
    int userAge;
    @Key(name = "guest_flag")
    boolean guestFlag;
    @Key(name = "progress")
    float progress;
    @Key(name = "search_history")
    Set<String> searchHistory;
}
