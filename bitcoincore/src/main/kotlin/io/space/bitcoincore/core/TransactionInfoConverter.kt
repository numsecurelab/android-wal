package io.space.bitcoincore.core

import io.space.bitcoincore.models.TransactionInfo
import io.space.bitcoincore.storage.FullTransactionInfo

class TransactionInfoConverter : ITransactionInfoConverter {
    override lateinit var baseConverter: BaseTransactionInfoConverter

    override fun transactionInfo(fullTransactionInfo: FullTransactionInfo): TransactionInfo {
        return baseConverter.transactionInfo(fullTransactionInfo)
    }
}
