package io.space.bitcoincore.transactions

import io.space.bitcoincore.core.IPluginData
import io.space.bitcoincore.managers.BloomFilterManager
import io.space.bitcoincore.storage.FullTransaction
import io.space.bitcoincore.storage.UnspentOutput
import io.space.bitcoincore.transactions.builder.TransactionBuilder

class TransactionCreator(
        private val builder: TransactionBuilder,
        private val processor: TransactionProcessor,
        private val transactionSender: TransactionSender,
        private val bloomFilterManager: BloomFilterManager) {

    @Throws
    fun create(toAddress: String, value: Long, feeRate: Int, senderPay: Boolean, pluginData: Map<Byte, IPluginData>): FullTransaction {
        return create {
            builder.buildTransaction(toAddress, value, feeRate, senderPay, pluginData)
        }
    }

    @Throws
    fun create(unspentOutput: UnspentOutput, toAddress: String, feeRate: Int): FullTransaction {
        return create {
            builder.buildTransaction(unspentOutput, toAddress, feeRate)
        }
    }

    private fun create(transactionBuilderFunction: () -> FullTransaction): FullTransaction {
        transactionSender.canSendTransaction()

        val transaction = transactionBuilderFunction.invoke()

        try {
            processor.processOutgoing(transaction)
        } catch (ex: BloomFilterManager.BloomFilterExpired) {
            bloomFilterManager.regenerateBloomFilter()
        }

        transactionSender.sendPendingTransactions()

        return transaction
    }

    open class TransactionCreationException(msg: String) : Exception(msg)
    class TransactionAlreadyExists(msg: String) : TransactionCreationException(msg)

}
