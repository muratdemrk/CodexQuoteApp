package com.murat.ozlusozler.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

public abstract class EventObserver<T> implements Observer<Event<T>> {

    @Override
    public void onChanged(Event<T> event) {
        if (event == null) {
            return;
        }

        T value = event.getContentIfNotHandled();
        if (value != null) {
            onEventUnhandledContent(value);
        }
    }

    public abstract void onEventUnhandledContent(@NonNull T value);
}

