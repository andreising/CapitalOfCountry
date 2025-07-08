package com.andreising.capitalofcountry.capital.presentation

import android.content.Context
import androidx.annotation.StringRes

interface ManageResource {

    fun string(@StringRes id: Int): String

    class Base(val context: Context) : ManageResource {
        override fun string(id: Int): String = context.getString(id)
    }
}