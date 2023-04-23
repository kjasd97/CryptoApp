package com.example.cryptoapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cryptoapp.CoinApplication
import com.example.cryptoapp.R
import com.example.cryptoapp.databinding.ActivityCoinDetailBinding
import com.example.cryptoapp.databinding.ActivityCoinPrceListBinding
import com.example.cryptoapp.domain.CoinInfo
import javax.inject.Inject


class CoinPriceListActivity : AppCompatActivity() {

    private lateinit var viewModel: CoinViewModel

    @Inject
    lateinit var viewModelFactory: CoinViewModelFactory

    private val binding by lazy {
        ActivityCoinPrceListBinding.inflate(layoutInflater)
    }

    private val component by lazy {
        (application as CoinApplication).component
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        component.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val adapter = CoinInfoAdapter(this)
        adapter.onCoinClickListener = object : CoinInfoAdapter.OnCoinClickListener {
            override fun onCoinClick(coinInfo: CoinInfo) {
                if (isOnePaneMode()){
                launchDetailActivity(coinInfo.fromSymbol)
                }else{
                    launchDetailFragment(coinInfo.fromSymbol)
                }
            }
        }
         binding.rvCoinPriceList.adapter = adapter
        binding.rvCoinPriceList.itemAnimator=null
        viewModel = ViewModelProvider(this, viewModelFactory)[CoinViewModel::class.java]
        viewModel.coinInfoList.observe(this, Observer {
            adapter.submitList(it)
        })
    }

    private fun isOnePaneMode() = binding.fragmentContainer == null

    private fun launchDetailActivity(fromSymbol:String){
        val intent =  CoinDetailActivity.newIntent(
            this@CoinPriceListActivity,
            fromSymbol
        )
        startActivity(intent)
    }

    private fun launchDetailFragment(fromSymbol:String){

        supportFragmentManager.popBackStack()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CoinDetailFragment.newInstance(fromSymbol))
            .addToBackStack(null)
            .commit()
    }
}
