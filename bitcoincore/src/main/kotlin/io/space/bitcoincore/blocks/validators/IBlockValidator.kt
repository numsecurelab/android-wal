package io.space.bitcoincore.blocks.validators

import io.space.bitcoincore.models.Block

interface IBlockValidator {
    fun validate(block: Block, previousBlock: Block)
}

interface IBlockChainedValidator : IBlockValidator {
    fun isBlockValidatable(block: Block, previousBlock: Block): Boolean
}
