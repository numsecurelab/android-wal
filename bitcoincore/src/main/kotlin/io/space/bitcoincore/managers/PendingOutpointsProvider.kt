package io.space.bitcoincore.managers

import io.space.bitcoincore.core.IStorage
import io.space.bitcoincore.utils.Utils

class PendingOutpointsProvider(private val storage: IStorage) : IBloomFilterProvider {

    override var bloomFilterManager: BloomFilterManager? = null

    override fun getBloomFilterElements(): List<ByteArray> {
        val incomingPendingTxHashes = storage.getIncomingPendingTxHashes()
        val inputs = storage.getTransactionInputs(incomingPendingTxHashes)

        return inputs.map { input -> input.previousOutputTxHash + Utils.intToByteArray(input.previousOutputIndex.toInt()).reversedArray() }
    }

}
