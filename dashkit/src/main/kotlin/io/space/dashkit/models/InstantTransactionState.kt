package io.space.dashkit.models

class InstantTransactionState {
    var instantTransactionHashes = mutableListOf<ByteArray>()

    fun append(hash: ByteArray) {
        instantTransactionHashes.add(hash)
    }
}
