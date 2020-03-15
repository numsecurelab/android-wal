package io.space.bitcoincore.transactions

import io.space.bitcoincore.core.IPluginData
import io.space.bitcoincore.managers.PublicKeyManager
import io.space.bitcoincore.transactions.builder.InputSetter
import io.space.bitcoincore.transactions.builder.MutableTransaction
import io.space.bitcoincore.transactions.builder.OutputSetter
import io.space.bitcoincore.transactions.scripts.ScriptType
import io.space.bitcoincore.utils.AddressConverterChain

class TransactionFeeCalculator(
        private val outputSetter: OutputSetter,
        private val inputSetter: InputSetter,
        private val addressConverter: AddressConverterChain,
        private val publicKeyManager: PublicKeyManager,
        private val changeScriptType: ScriptType
) {

    fun fee(value: Long, feeRate: Int, senderPay: Boolean, toAddress: String?, pluginData: Map<Byte, IPluginData>): Long {
        val mutableTransaction = MutableTransaction()

        outputSetter.setOutputs(mutableTransaction, toAddress ?: sampleAddress(), value, pluginData, true)
        inputSetter.setInputs(mutableTransaction, feeRate, senderPay)

        val inputsTotalValue = mutableTransaction.inputsToSign.map { it.previousOutput.value }.sum()
        val outputsTotalValue = mutableTransaction.recipientValue + mutableTransaction.changeValue

        return inputsTotalValue - outputsTotalValue
    }

    private fun sampleAddress(): String {
        return addressConverter.convert(publicKey = publicKeyManager.changePublicKey(), scriptType = changeScriptType).string
    }
}
