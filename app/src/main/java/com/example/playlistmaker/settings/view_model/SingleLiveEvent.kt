package com.example.playlistmaker.settings.view_model

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T>: MutableLiveData<T>() {
    private val pending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner){t ->
            if (pending.compareAndSet(true, false)) observer.onChanged(t)
        }
    }

    override fun setValue(value: T) {
        pending.set(true)
        super.setValue(value)
    }
}