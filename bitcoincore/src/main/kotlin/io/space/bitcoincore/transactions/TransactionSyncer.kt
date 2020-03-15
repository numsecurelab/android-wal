package io.space.bitcoincore.transactions

import io.space.bitcoincore.core.IStorage
import io.space.bitcoincore.managers.BloomFilterManager
import io.space.bitcoincore.managers.PublicKeyManager
import io.space.bitcoincore.storage.FullTransaction

class TransactionSyncer(
        private val storage: IStorage,
        private val transactionProcessor: TransactionProcessor,
        private val publicKeyManager: PublicKeyManager) {

    fun getNewTransactions(): List<FullTransaction> {
        return storage.getNewTransactions()
    }

    fun handleRelayed(transactions: List<FullTransaction>) {
        if (transactions.isEmpty()) return

        var needToUpdateBloomFilter = false

        try {
            transactionProcessor.processIncoming(transactions, null, false)
        } catch (e: BloomFilterManager.BloomFilterExpired) {
            needToUpdateBloomFilter = true
        }

        if (needToUpdateBloomFilter) {
            publicKeyManager.fillGap()
        }
    }

    fun shouldRequestTransaction(hash: ByteArray): Boolean {
        return !storage.isRelayedTransactionExists(hash)
    }

    fun handleInvalid(txHash: ByteArray) {
        transactionProcessor.processInvalid(txHash)
    }

}
