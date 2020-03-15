package io.space.bitcoincore.managers

import io.space.bitcoincore.core.IInitialSyncApi
import io.space.bitcoincore.extensions.toReversedByteArray
import io.space.bitcoincore.models.BlockHash
import io.space.bitcoincore.models.PublicKey

class BlockHashFetcher(private val restoreKeyConverter: IRestoreKeyConverter, private val initialSyncerApi: IInitialSyncApi, private val helper: BlockHashFetcherHelper) {

    fun getBlockHashes(publicKeys: List<PublicKey>): Pair<List<BlockHash>, Int> {
        val addresses = publicKeys.map {
            restoreKeyConverter.keysForApiRestore(it)
        }

        val transactions = initialSyncerApi.getTransactions(addresses.flatten())

        if (transactions.isEmpty()) {
            return Pair(listOf(), -1)
        }

        val lastUsedIndex = helper.lastUsedIndex(addresses, transactions.map { it.txOutputs }.flatten())

        val blockHashes = transactions.map {
            BlockHash(it.blockHash.toReversedByteArray(), it.blockHeight, 0)
        }

        return Pair(blockHashes, lastUsedIndex)
    }

}

class BlockHashFetcherHelper {

    fun lastUsedIndex(addresses: List<List<String>>, outputs: List<TransactionOutputItem>): Int {
        val searchAddressStrings = outputs.map { it.address }
        val searchScriptStrings = outputs.map { it.script }

        for (i in addresses.size - 1 downTo 0) {
            addresses[i].forEach { address ->
                if (searchAddressStrings.contains(address) || searchScriptStrings.any { script -> script.contains(address) }) {
                    return i
                }
            }
        }

        return -1
    }

}

data class TransactionItem(val blockHash: String, val blockHeight: Int, val txOutputs: List<TransactionOutputItem>)
data class TransactionOutputItem(val script: String, val address: String)
