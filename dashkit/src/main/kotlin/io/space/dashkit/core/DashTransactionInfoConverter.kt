package io.space.dashkit.core

import io.space.bitcoincore.core.BaseTransactionInfoConverter
import io.space.bitcoincore.core.ITransactionInfoConverter
import io.space.bitcoincore.extensions.toHexString
import io.space.bitcoincore.models.InvalidTransaction
import io.space.bitcoincore.models.Transaction
import io.space.bitcoincore.models.TransactionStatus
import io.space.bitcoincore.storage.FullTransactionInfo
import io.space.dashkit.instantsend.InstantTransactionManager
import io.space.dashkit.models.DashTransactionInfo

class DashTransactionInfoConverter(private val instantTransactionManager: InstantTransactionManager) : ITransactionInfoConverter {
    override lateinit var baseConverter: BaseTransactionInfoConverter

    override fun transactionInfo(fullTransactionInfo: FullTransactionInfo): DashTransactionInfo {
        val transaction = fullTransactionInfo.header

        if (transaction.status == Transaction.Status.INVALID) {
            (transaction as? InvalidTransaction)?.let {
                return getInvalidTransactionInfo(it)
            }
        }

        val txInfo = baseConverter.transactionInfo(fullTransactionInfo)

        return DashTransactionInfo(
                txInfo.uid,
                txInfo.transactionHash,
                txInfo.transactionIndex,
                txInfo.inputs,
                txInfo.outputs,
                txInfo.fee,
                txInfo.blockHeight,
                txInfo.timestamp,
                txInfo.status,
                txInfo.conflictingTxHash,
                instantTransactionManager.isTransactionInstant(fullTransactionInfo.header.hash)
        )
    }

    private fun getInvalidTransactionInfo(transaction: InvalidTransaction): DashTransactionInfo {
        return try {
            DashTransactionInfo(transaction.serializedTxInfo)
        } catch (ex: Exception) {
            DashTransactionInfo(
                    uid = transaction.uid,
                    transactionHash = transaction.hash.toHexString(),
                    transactionIndex = transaction.order,
                    timestamp = transaction.timestamp,
                    status = TransactionStatus.INVALID,
                    inputs = listOf(),
                    outputs = listOf(),
                    fee = null,
                    blockHeight = null,
                    conflictingTxHash = null,
                    instantTx = false
            )
        }
    }

}