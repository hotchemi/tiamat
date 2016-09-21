# Tiamat

[![Build Status](https://travis-ci.org/hotchemi/tiamat.svg?branch=master)](https://travis-ci.org/hotchemi/tiamat)

Tiamat is an Android library generates **Reactive SharedPreferences** code.

### Motivation

- `SharedPreferences` tends to be a mess in a long development history.
  - We need a way to manage keys effectively like database definition in ORM.
- `SharedPreferences` is just a data so that we wanna deal with them as `Observable` or `Action` of RxJava.

## Usage

### Define keys

You can define preference keys like the following.

Currently tiamat supports `boolean`, `String`, `float`, `int`, `long` and `Set<String>` types.

```java
@Pref("sample")
class Sample {
    @Key(name = "long_value")
    long longValue = false;
    // you can define default value like stringValue
    @Key(name = "string_value")
    String stringValue = "default_value";
    @Key(name = "boolean_value")
    boolean booleanValue;
    @Key(name = "int_value")
    int intValue;
    @Key(name = "float_value")
    float floatValue;
    @Key(name = "set_string")
    Set<String> setStringValue;
}
```

### Use generated code

Annotation processor automatically generates `XXXSharedPreferences` class after rebuild the project.

You can use generated `put`, `get`, `has`, `remove` methods.

```java
SampleSharedPreferences preferences = new SampleSharedPreferences(context);
preferences.putStringValue(string);
preferences.putStringValue(string, defaultValue);
preferences.getStringValue();
preferences.hasStringValue();
preferences.removeStringValue();
```

## With Rx

`getXXX` method returns proxy object. If you want to use the value itself, just call `asValue()`.

```java
boolean value = preferences.getBooleanValue().asValue();
```

You can use `asObservable()` method to get a RxJava Observable.

```java
Observable<Boolean> value = preferences.getBooleanValue().asObservable();
```

You can also use `asAction()` method to get an Action that update value.

```java
Action1<? super Boolean> value = preferences.getBooleanValue().asAction();
```

It means you can monitor the change of Preferences automatically and compound actions with other Rx library.

Please check [sample app](https://github.com/hotchemi/tiamat/tree/master/sample) for more details.

## Install

To add to your project, include the following in your **project** `build.gradle` file:

```groovy
buildscript {
  dependencies {
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
  }
}
```

And on your **app module** `build.gradle`:

`${latest.version}` is [![Download](https://api.bintray.com/packages/hotchemi/maven/tiamat/images/download.svg)](https://bintray.com/hotchemi/maven/tiamat/_latestVersion)

```groovy
apply plugin: 'android-apt'

dependencies {
  compile 'com.github.hotchemi:tiamat:${latest.version}'
  provided 'com.github.hotchemi:tiamat-compiler:${latest.version}'
}
```

## Note

I got the idea from [kvs-schema](https://github.com/rejasupotaro/kvs-schema), [rx-preferences](https://github.com/f2prateek/rx-preferences).

Really appreciate for two amazing libraries.

## Licence

```
Copyright 2016 Shintaro Katafuchi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
