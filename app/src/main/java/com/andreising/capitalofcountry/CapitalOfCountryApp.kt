package com.andreising.capitalofcountry

import android.app.Application
import com.andreising.capitalofcountry.capital.data.cloud.CapitalCloudDataSource
import com.andreising.capitalofcountry.capital.data.cloud.CapitalService
import com.andreising.capitalofcountry.capital.data.cloud.CloudErrorHandler
import com.andreising.capitalofcountry.capital.data.cloud.CloudModule

class CapitalOfCountryApp : Application() {
    private lateinit var cloudDataSource: CapitalCloudDataSource
    override fun onCreate() {
        super.onCreate()
        cloudDataSource = CapitalCloudDataSource.Base(
            CloudModule.Base(BuildConfig.DEBUG).service(CapitalService::class.java),
            cloudErrorHandler = CloudErrorHandler.Base()
        )
    }
}