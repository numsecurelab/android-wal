package io.space.bitcoincore.managers

import io.space.bitcoincore.core.IStorage
import io.space.bitcoincore.models.Block

open class BlockValidatorHelper(protected val storage: IStorage) {
    fun getPreviousChunk(blockHeight: Int, size: Int): List<Block> {
        return storage.getBlocksChunk(fromHeight = blockHeight - size, toHeight = blockHeight)
    }

    fun getPrevious(block: Block, stepBack: Int): Block? {
        return storage.getBlock(block.height - stepBack)
    }
}
