package io.space.dashkit.masternodelist

import io.space.bitcoincore.core.HashBytes
import io.space.bitcoincore.core.IHasher
import io.space.dashkit.models.CoinbaseTransaction
import io.space.dashkit.models.CoinbaseTransactionSerializer

class MasternodeCbTxHasher(private val coinbaseTransactionSerializer: CoinbaseTransactionSerializer, private val hasher: IHasher) {

    fun hash(coinbaseTransaction: CoinbaseTransaction): HashBytes {
        val serialized = coinbaseTransactionSerializer.serialize(coinbaseTransaction)

        return HashBytes(hasher.hash(serialized))
    }

}
