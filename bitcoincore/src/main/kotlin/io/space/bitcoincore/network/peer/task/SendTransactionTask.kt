package io.space.bitcoincore.network.peer.task

import io.space.bitcoincore.extensions.toReversedHex
import io.space.bitcoincore.models.InventoryItem
import io.space.bitcoincore.network.messages.GetDataMessage
import io.space.bitcoincore.network.messages.IMessage
import io.space.bitcoincore.network.messages.InvMessage
import io.space.bitcoincore.network.messages.TransactionMessage
import io.space.bitcoincore.storage.FullTransaction

class SendTransactionTask(val transaction: FullTransaction) : PeerTask() {

    init {
        allowedIdleTime = 30
    }

    override val state: String
        get() = "transaction: ${transaction.header.hash.toReversedHex()}"

    override fun start() {
        requester?.send(InvMessage(InventoryItem.MSG_TX, transaction.header.hash))
        resetTimer()
    }

    override fun handleMessage(message: IMessage): Boolean {
        val transactionRequested =
                message is GetDataMessage &&
                message.inventory.any { it.type == InventoryItem.MSG_TX && it.hash.contentEquals(transaction.header.hash) }

        if (transactionRequested) {
            requester?.send(TransactionMessage(transaction, 0))
            listener?.onTaskCompleted(this)
        }

        return transactionRequested
    }

    override fun handleTimeout() {
        listener?.onTaskCompleted(this)
    }

}
