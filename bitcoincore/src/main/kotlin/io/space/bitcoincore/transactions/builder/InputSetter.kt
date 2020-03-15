package io.space.bitcoincore.transactions.builder

import io.space.bitcoincore.DustCalculator
import io.space.bitcoincore.core.PluginManager
import io.space.bitcoincore.managers.IUnspentOutputSelector
import io.space.bitcoincore.managers.PublicKeyManager
import io.space.bitcoincore.models.TransactionInput
import io.space.bitcoincore.storage.InputToSign
import io.space.bitcoincore.storage.UnspentOutput
import io.space.bitcoincore.transactions.TransactionSizeCalculator
import io.space.bitcoincore.transactions.scripts.ScriptType
import io.space.bitcoincore.utils.Bip69
import io.space.bitcoincore.utils.IAddressConverter
import java.util.*

class InputSetter(
        private val unspentOutputSelector: IUnspentOutputSelector,
        private val publicKeyManager: PublicKeyManager,
        private val addressConverter: IAddressConverter,
        private val changeScriptType: ScriptType,
        private val transactionSizeCalculator: TransactionSizeCalculator,
        private val pluginManager: PluginManager,
        private val dustCalculator: DustCalculator
) {
    fun setInputs(mutableTransaction: MutableTransaction, feeRate: Int, senderPay: Boolean) {
        val value = mutableTransaction.recipientValue
        val dust = dustCalculator.dust(changeScriptType)
        val unspentOutputInfo = unspentOutputSelector.select(
                value,
                feeRate,
                mutableTransaction.recipientAddress.scriptType,
                changeScriptType,
                senderPay, dust,
                mutableTransaction.getPluginDataOutputSize()
        )

        val unspentOutputs = unspentOutputInfo.outputs

        Collections.sort(unspentOutputs, Bip69.inputComparator)

        for (unspentOutput in unspentOutputs) {
            mutableTransaction.addInput(inputToSign(unspentOutput))
        }

        mutableTransaction.recipientValue = unspentOutputInfo.recipientValue

        // Add change output if needed
        unspentOutputInfo.changeValue?.let { changeValue ->
            val changePubKey = publicKeyManager.changePublicKey()
            val changeAddress = addressConverter.convert(changePubKey, changeScriptType)

            mutableTransaction.changeAddress = changeAddress
            mutableTransaction.changeValue = changeValue
        }

        pluginManager.processInputs(mutableTransaction)
    }

    fun setInputs(mutableTransaction: MutableTransaction, unspentOutput: UnspentOutput, feeRate: Int) {
        if (unspentOutput.output.scriptType != ScriptType.P2SH) {
            throw TransactionBuilder.BuilderException.NotSupportedScriptType()
        }

        // Calculate fee
        val transactionSize = transactionSizeCalculator.transactionSize(listOf(unspentOutput.output), listOf(mutableTransaction.recipientAddress.scriptType), 0)
        val fee = transactionSize * feeRate

        if (unspentOutput.output.value < fee) {
            throw TransactionBuilder.BuilderException.FeeMoreThanValue()
        }

        // Add to mutable transaction
        mutableTransaction.addInput(inputToSign(unspentOutput))
        mutableTransaction.recipientValue = unspentOutput.output.value - fee
    }

    private fun inputToSign(unspentOutput: UnspentOutput): InputToSign {
        val previousOutput = unspentOutput.output
        val transactionInput = TransactionInput(previousOutput.transactionHash, previousOutput.index.toLong())

        if (unspentOutput.output.scriptType == ScriptType.P2WPKH) {
            unspentOutput.output.keyHash = unspentOutput.output.keyHash?.drop(2)?.toByteArray()
        }

        val inputToSign = InputToSign(transactionInput, previousOutput, unspentOutput.publicKey)
        return inputToSign
    }
}
