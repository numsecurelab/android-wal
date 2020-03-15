package io.space.bitcoincore.transactions

import io.space.bitcoincore.blocks.IPeerSyncListener

class SendTransactionsOnPeersSynced(var transactionSender: TransactionSender) : IPeerSyncListener {

    override fun onAllPeersSynced() {
        transactionSender.sendPendingTransactions()
    }

}

