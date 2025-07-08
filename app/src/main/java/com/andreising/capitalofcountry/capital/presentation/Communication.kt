package com.andreising.capitalofcountry.capital.presentation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

interface Communication {

    interface Observe<T> {
        fun observe(owner: LifecycleOwner, observer: Observer<T>)
    }

    interface Update<T> : Mapper.Unit<T>

    interface Mutable<T> : Observe<T>, Update<T>

    abstract class Abstract<T>(
        protected open val mutableLiveData: MutableLiveData<T>
    ) : Mutable<T> {
        override fun observe(owner: LifecycleOwner, observer: Observer<T>) =
            mutableLiveData.observe(owner, observer)
    }

    abstract class Ui<T>(override val mutableLiveData: MutableLiveData<T> = MutableLiveData<T>()) :
        Abstract<T>(mutableLiveData = mutableLiveData) {
        override fun map(source: T) {
            mutableLiveData.value = source
        }
    }

    abstract class Post<T>(override val mutableLiveData: MutableLiveData<T> = MutableLiveData<T>()) :
        Abstract<T>(mutableLiveData = mutableLiveData) {
        override fun map(source: T) = mutableLiveData.postValue(source)
    }
}