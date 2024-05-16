// IWakeService.aidl
package io.github.xposedev.aidl;

import io.github.xposedev.aidl.IMessageCallback;

import io.github.xposedev.aidl.Message;

// Declare any non-default types here with import statements
interface IWakeService {

    void rgigster(IMessageCallback callback);

    void unregister(IMessageCallback callback);

    // void sendMessage(in Message message);

     void sendMessage(in String tag, in String message);

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}