package io.space.bitcoinkit.demo

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.space.bitcoincore.BitcoinCore
import java.text.SimpleDateFormat
import java.util.*

class BalanceFragment : Fragment() {

    lateinit var viewModel: MainViewModel
    lateinit var networkName: TextView
    lateinit var balanceValue: TextView
    lateinit var balanceUnspendableValue: TextView
    lateinit var lastBlockDateValue: TextView
    lateinit var lastBlockValue: TextView
    lateinit var stateValue: TextView
    lateinit var startButton: Button
    lateinit var clearButton: Button
    lateinit var buttonDebug: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            viewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)

            viewModel.balance.observe(this, Observer { balance ->
                when (balance) {
                    null -> {
                        balanceValue.text = ""
                        balanceUnspendableValue.text = ""
                    }
                    else -> {
                        balanceValue.text = NumberFormatHelper.cryptoAmountFormat.format(balance.spendable / 100_000_000.0)
                        balanceUnspendableValue.text = NumberFormatHelper.cryptoAmountFormat.format(balance.unspendable / 100_000_000.0)
                    }
                }
            })

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            viewModel.lastBlock.observe(this, Observer {
                it?.let { blockInfo ->
                    lastBlockValue.text = blockInfo.height.toString()

                    val strDate = dateFormat.format(Date(blockInfo.timestamp * 1000))
                    lastBlockDateValue.text = strDate
                }
            })

            viewModel.state.observe(this, Observer { state ->
                when (state) {
                    is BitcoinCore.KitState.Synced -> {
                        stateValue.text = "synced"
                    }
                    is BitcoinCore.KitState.Syncing -> {
                        stateValue.text = "syncing ${"%.3f".format(state.progress)}"
                    }
                    is BitcoinCore.KitState.NotSynced -> {
                        stateValue.text = "not synced"
                    }
                }
            })

            viewModel.status.observe(this, Observer {
                when (it) {
                    MainViewModel.State.STARTED -> {
                        startButton.isEnabled = false
                    }
                    else -> {
                        startButton.isEnabled = true
                    }
                }
            })

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_balance, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        networkName = view.findViewById(R.id.networkName)
        networkName.text = viewModel.networkName

        balanceValue = view.findViewById(R.id.balanceValue)
        balanceUnspendableValue = view.findViewById(R.id.balanceUnspendableValue)
        lastBlockValue = view.findViewById(R.id.lastBlockValue)
        lastBlockDateValue = view.findViewById(R.id.lastBlockDateValue)
        stateValue = view.findViewById(R.id.stateValue)
        startButton = view.findViewById(R.id.buttonStart)
        clearButton = view.findViewById(R.id.buttonClear)
        buttonDebug = view.findViewById(R.id.buttonDebug)

        startButton.setOnClickListener {
            viewModel.start()
        }

        clearButton.setOnClickListener {
            viewModel.clear()
        }

        buttonDebug.setOnClickListener {
            viewModel.showDebugInfo()
        }
    }
}
