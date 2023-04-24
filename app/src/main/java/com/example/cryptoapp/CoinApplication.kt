package com.example.cryptoapp

import android.app.Application
import androidx.work.Configuration
import com.example.cryptoapp.data.database.AppDatabase
import com.example.cryptoapp.data.mapper.CoinMapper
import com.example.cryptoapp.data.network.ApiFactory
import com.example.cryptoapp.data.network.workers.RefreshDataWorkerFactory
import com.example.cryptoapp.di.DaggerApplicationComponent
import javax.inject.Inject

class CoinApplication : Application(), Configuration.Provider {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    @Inject
    lateinit var workerFactory: RefreshDataWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory).build()
    }

    override fun onCreate() {

        component.inject(this)

        super.onCreate()

    }

}