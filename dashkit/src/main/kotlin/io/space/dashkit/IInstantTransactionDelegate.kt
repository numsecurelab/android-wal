package io.space.dashkit

// TODO Rename to listener
interface IInstantTransactionDelegate {
    fun onUpdateInstant(transactionHash: ByteArray)
}
