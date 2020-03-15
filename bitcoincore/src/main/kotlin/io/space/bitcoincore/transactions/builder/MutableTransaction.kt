package io.space.bitcoincore.transactions.builder

import io.space.bitcoincore.models.Address
import io.space.bitcoincore.models.Transaction
import io.space.bitcoincore.models.TransactionOutput
import io.space.bitcoincore.storage.FullTransaction
import io.space.bitcoincore.storage.InputToSign
import io.space.bitcoincore.transactions.scripts.OP_RETURN
import io.space.bitcoincore.transactions.scripts.ScriptType
import io.space.bitcoincore.utils.Bip69
import java.util.*

class MutableTransaction(isOutgoing: Boolean = true) {

    val inputsToSign = mutableListOf<InputToSign>()
    val transaction = Transaction(2, 0)

    lateinit var recipientAddress: Address
    var recipientValue = 0L

    var changeAddress: Address? = null
    var changeValue = 0L

    private val pluginData = mutableMapOf<Byte, ByteArray>()

    val outputs: List<TransactionOutput>
        get() {
            val list = mutableListOf<TransactionOutput>()

            recipientAddress.let {
                list.add(TransactionOutput(recipientValue, 0, it.lockingScript, it.scriptType, it.string, it.hash))
            }

            changeAddress?.let {
                list.add(TransactionOutput(changeValue, 0, it.lockingScript, it.scriptType, it.string, it.hash))
            }

            if (pluginData.isNotEmpty()) {
                var data = byteArrayOf(OP_RETURN.toByte())
                pluginData.forEach {
                    data += byteArrayOf(it.key) + it.value
                }

                list.add(TransactionOutput(0, 0, data, ScriptType.NULL_DATA))
            }

            Collections.sort(list, Bip69.outputComparator)

            list.forEachIndexed { index, transactionOutput ->
                transactionOutput.index = index
            }

            return list
        }

    init {
        transaction.status = Transaction.Status.NEW
        transaction.isMine = true
        transaction.isOutgoing = isOutgoing
    }

    fun getPluginDataOutputSize(): Int {
        return if (pluginData.isNotEmpty()) {
            1 + pluginData.map { 1 + it.value.size }.sum()
        } else {
            0
        }
    }

    fun addInput(inputToSign: InputToSign) {
        inputsToSign.add(inputToSign)
    }

    fun addPluginData(id: Byte, data: ByteArray) {
        pluginData[id] = data
    }

    fun build(): FullTransaction {
        return FullTransaction(transaction, inputsToSign.map { it.input }, outputs)
    }

}
