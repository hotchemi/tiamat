package tiamat.sample;

import java.util.Set;

import tiamat.Key;
import tiamat.Pref;

@Pref("status")
class Status {
    // stores checkbox state
    @Key(name = "checked")
    boolean checked = false;

    // supports the following types
    @Key(name = "long_value")
    long longValue;
    @Key(name = "string_value")
    String stringValue;
    @Key(name = "boolean_value")
    boolean booleanValue;
    @Key(name = "int_value")
    int intValue;
    @Key(name = "float_value")
    float floatValue;
    @Key(name = "set_string")
    Set<String> setStringValue;
}
