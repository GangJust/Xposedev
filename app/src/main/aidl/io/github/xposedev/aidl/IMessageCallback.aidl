// IMessageCallback.aidl
package io.github.xposedev.aidl;

import io.github.xposedev.aidl.Message;

// Declare any non-default types here with import statements
interface IMessageCallback {
    // void callback(in List<Message> message);

    void callback(in String tag, in String message);
}