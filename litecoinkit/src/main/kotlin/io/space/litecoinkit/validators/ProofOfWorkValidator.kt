package io.space.litecoinkit.validators

import io.space.bitcoincore.blocks.validators.BlockValidatorException
import io.space.bitcoincore.blocks.validators.IBlockChainedValidator
import io.space.bitcoincore.crypto.CompactBits
import io.space.bitcoincore.extensions.toHexString
import io.space.bitcoincore.io.BitcoinOutput
import io.space.bitcoincore.models.Block
import io.space.litecoinkit.ScryptHasher
import java.math.BigInteger

class ProofOfWorkValidator(private val scryptHasher: ScryptHasher) : IBlockChainedValidator {

    override fun validate(block: Block, previousBlock: Block) {
        val blockHeaderData = getSerializedBlockHeader(block)

        val powHash = scryptHasher.hash(blockHeaderData).toHexString()

        check(BigInteger(powHash, 16) < CompactBits.decode(block.bits)) {
            throw BlockValidatorException.InvalidProofOfWork()
        }
    }

    private fun getSerializedBlockHeader(block: Block): ByteArray {
        return BitcoinOutput()
                .writeInt(block.version)
                .write(block.previousBlockHash)
                .write(block.merkleRoot)
                .writeUnsignedInt(block.timestamp)
                .writeUnsignedInt(block.bits)
                .writeUnsignedInt(block.nonce)
                .toByteArray()
    }

    override fun isBlockValidatable(block: Block, previousBlock: Block): Boolean {
        return true
    }

}
