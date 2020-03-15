package io.space.bitcoincore.blocks

import io.space.bitcoincore.models.Block
import io.space.bitcoincore.models.Transaction

interface IBlockchainDataListener {
    fun onBlockInsert(block: Block)
    fun onTransactionsUpdate(inserted: List<Transaction>, updated: List<Transaction>, block: Block?)
    fun onTransactionsDelete(hashes: List<String>)
}
