package io.space.bitcoincore.blocks.validators

import io.space.bitcoincore.crypto.CompactBits
import io.space.bitcoincore.extensions.toReversedHex
import io.space.bitcoincore.models.Block
import java.math.BigInteger

class ProofOfWorkValidator : IBlockChainedValidator {

    override fun isBlockValidatable(block: Block, previousBlock: Block): Boolean {
        return true
    }

    override fun validate(block: Block, previousBlock: Block) {
        check(BigInteger(block.headerHash.toReversedHex(), 16) < CompactBits.decode(block.bits)) {
            throw BlockValidatorException.InvalidProofOfWork()
        }
    }
}
