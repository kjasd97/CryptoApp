package com.example.cryptoapp.data.network.workers

import android.content.Context
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.work.*
import com.example.cryptoapp.data.database.AppDatabase
import com.example.cryptoapp.data.mapper.CoinMapper
import com.example.cryptoapp.data.network.ApiFactory
import kotlinx.coroutines.delay

class RefreshDataWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {


    private val databaseMethods = AppDatabase.getInstance(context).coinInfoDao()
    private val apiService = ApiFactory.apiService
    private val mapper = CoinMapper()

    override suspend fun doWork(): Result {
        while (true) {
            try {
                val topCoins = apiService.getTopCoinsInfo(limit = 50)
                val fSyms = mapper.namesListDtoToString(topCoins)
                val jsonContainer = apiService.getFullPriceList(fSyms = fSyms)
                val coinInfoDtoList = mapper.mapJsonContainerToListCoinInfo(jsonContainer)
                val dbModelList = coinInfoDtoList.map { mapper.mapDtoToDbModel(it) }
                databaseMethods.insertCoinInfoList(dbModelList)
            } catch (e: Exception) {
            }
            delay(10000)
        }
    }

    companion object{
        const val NAME ="name"

        fun oneTimeWorkRequest():OneTimeWorkRequest{
            return OneTimeWorkRequestBuilder<RefreshDataWorker>()
                .setConstraints(constraints())
                .build()
        }

        private fun constraints ():Constraints{
            return  Constraints.Builder()
                .setRequiresCharging(true)
                .build()
        }

    }


}