package io.space.bitcoincore.transactions.builder

import io.space.bitcoincore.core.IPluginData
import io.space.bitcoincore.core.PluginManager
import io.space.bitcoincore.utils.IAddressConverter

class OutputSetter(private val addressConverter: IAddressConverter, private val pluginManager: PluginManager) {

    fun setOutputs(mutableTransaction: MutableTransaction, addressStr: String, value: Long, pluginData: Map<Byte, IPluginData>, skipChecking: Boolean = false) {
        mutableTransaction.recipientAddress = addressConverter.convert(addressStr)
        mutableTransaction.recipientValue = value

        pluginManager.processOutputs(mutableTransaction, pluginData, skipChecking)
    }

}
