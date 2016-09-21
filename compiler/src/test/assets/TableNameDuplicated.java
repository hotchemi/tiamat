package tiamat.compiler;

import tiamat.Key;
import tiamat.Pref;

@Pref("foo")
public class TableNameDuplicated {

    @Pref("foo")
    public static class Foo {
        @Key(name = "user_id")
        long userId;
    }

    @Key(name = "user_id")
    long userId;
}
