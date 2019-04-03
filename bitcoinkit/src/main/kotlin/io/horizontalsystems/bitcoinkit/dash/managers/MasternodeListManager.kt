package io.horizontalsystems.bitcoinkit.dash.managers

import io.horizontalsystems.bitcoinkit.dash.IDashStorage
import io.horizontalsystems.bitcoinkit.dash.masternodelist.MasternodeCbTxHasher
import io.horizontalsystems.bitcoinkit.dash.masternodelist.MasternodeListMerkleRootCalculator
import io.horizontalsystems.bitcoinkit.dash.messages.MasternodeListDiffMessage
import io.horizontalsystems.bitcoinkit.dash.models.MasternodeListState
import io.horizontalsystems.bitcoinkit.extensions.toReversedHex
import io.horizontalsystems.bitcoinkit.utils.HashUtils
import io.horizontalsystems.bitcoinkit.utils.MerkleBranch

class MasternodeListManager(
        private val storage: IDashStorage,
        private val masternodeListMerkleRootCalculator: MasternodeListMerkleRootCalculator,
        private val masternodeCbTxHasher: MasternodeCbTxHasher,
        private val merkleBranch: MerkleBranch,
        private val masternodeSortedList: MasternodeSortedList
) {

    open class ValidationError : Exception() {
        object WrongMerkleRootList : ValidationError()
        object WrongCoinbaseHash : ValidationError()
        object NoMerkleBlockHeader : ValidationError()
        object WrongMerkleRoot : ValidationError()
    }

    val baseBlockHash: ByteArray
        get() {
            return storage.masternodeListState?.baseBlockHash
                    ?: HashUtils.toBytesAsLE("0000000000000000000000000000000000000000000000000000000000000000")
        }

    //01. Create a copy of the masternode list which was valid at “baseBlockHash”. If “baseBlockHash” is all-zero, an empty list must be used.
    //02. Delete all entries found in “deletedMNs” from this list. Please note that “deletedMNs” contains the ProRegTx hashes of the masternodes and NOT the hashes of the SML entries.
    //03. Add or replace all entries found in “mnList” in the list
    //04. Calculate the merkle root of the list by following the “Calculating the merkle root of the Masternode list” section
    //05. Compare the calculated merkle root with what is found in “cbTx”. If it does not match, abort the process and ask for diffs from another node.
    //06. Calculate the hash of “cbTx” and verify existence of this transaction in the block specified by “blockHash”. To do this, use the already received block header and the fields “totalTransactions”, “merkleHashes” and “merkleFlags” from the MNLISTDIFF message and perform a merkle verification the same way as done when a “MERKLEBLOCK” message is received. If the verification fails, abort the process and ask for diffs from another node.
    //07. Store the resulting validated masternode list identified by “blockHash”
    @Throws(ValidationError::class)
    fun updateList(masternodeListDiffMessage: MasternodeListDiffMessage) {
        masternodeSortedList.removeAll()

        //01.
        masternodeSortedList.add(storage.masternodes)
        //02.
        masternodeSortedList.remove(masternodeListDiffMessage.deletedMNs)
        //03.
        masternodeSortedList.add(masternodeListDiffMessage.mnList)
        //04.
        val hash = masternodeListMerkleRootCalculator.calculateMerkleRoot(masternodeSortedList.masternodes)

        //05.
        if (hash != null && !masternodeListDiffMessage.cbTx.merkleRootMNList.contentEquals(hash)) {
            throw ValidationError.WrongMerkleRootList
        }
        //06.
        val cbTxHash = masternodeCbTxHasher.hash(masternodeListDiffMessage.cbTx)

        val matchedHashes = mutableListOf<ByteArray>()

        val calculatedMerkleRoot = merkleBranch.calculateMerkleRoot(masternodeListDiffMessage.totalTransactions.toInt(), masternodeListDiffMessage.merkleHashes, masternodeListDiffMessage.merkleFlags, matchedHashes)

        if (matchedHashes.none { it.contentEquals(cbTxHash) }) {
            throw ValidationError.WrongCoinbaseHash
        }

        val block = storage.getBlock(masternodeListDiffMessage.blockHash.toReversedHex())
        val merkleRoot = block?.merkleRoot

        if (block == null || merkleRoot == null) {
            throw ValidationError.NoMerkleBlockHeader
        }

        if (!merkleRoot.contentEquals(calculatedMerkleRoot)) {
            throw ValidationError.WrongMerkleRoot
        }

        //07.
        storage.masternodeListState = MasternodeListState(masternodeListDiffMessage.blockHash)
        storage.masternodes = masternodeSortedList.masternodes
        // todo: Can optimize. Update only difference of masternode list
    }

}
