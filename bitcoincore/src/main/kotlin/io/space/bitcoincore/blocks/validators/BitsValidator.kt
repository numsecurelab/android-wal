package io.space.bitcoincore.blocks.validators

import io.space.bitcoincore.models.Block

class BitsValidator : IBlockChainedValidator {

    override fun isBlockValidatable(block: Block, previousBlock: Block): Boolean {
        return true
    }

    override fun validate(block: Block, previousBlock: Block) {
        if (block.bits != previousBlock.bits) {
            throw BlockValidatorException.NotEqualBits()
        }
    }

}
