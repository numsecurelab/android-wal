package io.space.dashkit.tasks

import io.space.bitcoincore.models.InventoryItem
import io.space.bitcoincore.network.messages.GetDataMessage
import io.space.bitcoincore.network.messages.IMessage
import io.space.bitcoincore.network.peer.task.PeerTask
import io.space.dashkit.InventoryType
import io.space.dashkit.messages.TransactionLockVoteMessage

class RequestTransactionLockVotesTask(hashes: List<ByteArray>) : PeerTask() {

    val hashes = hashes.toMutableList()
    var transactionLockVotes = mutableListOf<TransactionLockVoteMessage>()

    override fun start() {
        val items = hashes.map { hash ->
            InventoryItem(InventoryType.MSG_TXLOCK_VOTE, hash)
        }

        requester?.send(GetDataMessage(items))
    }

    override fun handleMessage(message: IMessage) = when (message) {
        is TransactionLockVoteMessage -> handleTransactionLockVote(message)
        else -> false
    }

    private fun handleTransactionLockVote(transactionLockVote: TransactionLockVoteMessage): Boolean {
        val hash = hashes.firstOrNull { it.contentEquals(transactionLockVote.hash) } ?: return false

        hashes.remove(hash)
        transactionLockVotes.add(transactionLockVote)

        if (hashes.isEmpty()) {
            listener?.onTaskCompleted(this)
        }

        return true
    }

}
