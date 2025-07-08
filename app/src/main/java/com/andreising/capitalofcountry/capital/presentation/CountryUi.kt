package com.andreising.capitalofcountry.capital.presentation

import android.widget.ImageView
import android.widget.TextView

data class CountryUi(
    private val name: String,
    private val capital: String,
    private val region: String,
    private val languages: String,
    private val flagLink: String,
    private val currencyInfo: String
) {
    fun bind(
        nameTv: TextView,
        capitalTv: TextView,
        regionTv: TextView,
        languagesTv: TextView,
        flagImageView: ImageView,
        currencyTv: TextView
    ) {
        nameTv.text = name
        capitalTv.text = capital
        regionTv.text = region
        languagesTv.text = languages
        //todo add image bind by ling
        currencyTv.text = currencyInfo
    }
}
