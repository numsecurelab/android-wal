package io.space.bitcoincore.network.peer.task

import io.space.bitcoincore.models.InventoryItem
import io.space.bitcoincore.network.messages.GetDataMessage
import io.space.bitcoincore.network.messages.IMessage
import io.space.bitcoincore.network.messages.TransactionMessage
import io.space.bitcoincore.storage.FullTransaction

class RequestTransactionsTask(hashes: List<ByteArray>) : PeerTask() {
    val hashes = hashes.toMutableList()
    var transactions = mutableListOf<FullTransaction>()

    override val state: String
        get() =
            "hashesCount: ${hashes.size}; receivedTransactionsCount: ${transactions.size}"

    override fun start() {
        val items = hashes.map { hash ->
            InventoryItem(InventoryItem.MSG_TX, hash)
        }

        requester?.send(GetDataMessage(items))
        resetTimer()
    }

    override fun handleMessage(message: IMessage): Boolean {
        if (message !is TransactionMessage) {
            return false
        }

        val transaction = message.transaction
        val hash = hashes.firstOrNull { it.contentEquals(transaction.header.hash) } ?: return false

        hashes.remove(hash)
        transactions.add(transaction)

        if (hashes.isEmpty()) {
            listener?.onTaskCompleted(this)
        }

        return true
    }

}
