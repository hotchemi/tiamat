package tiamat.compiler;

import tiamat.Key;
import tiamat.Pref;

@Pref
public class NoTableName {
    @Key(name = "user_id")
    long userId;
    @Key
    String userName;
}