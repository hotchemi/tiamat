package tiamat.sample;

import tiamat.Key;
import tiamat.Pref;

import java.util.Set;

// If no `name` parameter is provided(or empty), tiamat can automatically generate SharedPreferences name.
// In this case, generated name will be `no_name_status`.
@Pref
public class NoNameStatus {
    // key name can also be auto generated.
    @Key
    long longValue;
    @Key
    final String stringValue = "default_value";
    @Key
    boolean booleanValue;
    @Key
    int intValue;
    @Key
    float floatValue;
    @Key
    Set<String> setStringValue;
}
