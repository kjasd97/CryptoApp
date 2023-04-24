package com.example.cryptoapp.data.network.workers

import android.content.Context
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.work.*
import com.example.cryptoapp.data.database.AppDatabase
import com.example.cryptoapp.data.database.CoinInfoDao
import com.example.cryptoapp.data.mapper.CoinMapper
import com.example.cryptoapp.data.network.ApiFactory
import com.example.cryptoapp.data.network.ApiService
import kotlinx.coroutines.delay

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val databaseMethods: CoinInfoDao,
    private val apiService : ApiService,
    private val mapper : CoinMapper

) :
    CoroutineWorker(context, workerParameters) {


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