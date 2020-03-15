package io.space.bitcoincore.serializers

import io.space.bitcoincore.core.IHasher
import io.space.bitcoincore.io.BitcoinInput
import io.space.bitcoincore.io.BitcoinOutput
import io.space.bitcoincore.storage.BlockHeader

class BlockHeaderParser(private val hasher: IHasher) {

    fun parse(input: BitcoinInput): BlockHeader {
        val version = input.readInt()
        val previousBlockHeaderHash = input.readBytes(32)
        val merkleRoot = input.readBytes(32)
        val timestamp = input.readUnsignedInt()
        val bits = input.readUnsignedInt()
        val nonce = input.readUnsignedInt()

        val payload = serialize(version, previousBlockHeaderHash, merkleRoot, timestamp, bits, nonce)

        val hash = hasher.hash(payload)

        return BlockHeader(version, previousBlockHeaderHash, merkleRoot, timestamp, bits, nonce, hash)
    }

    private fun serialize(version: Int, previousBlockHeaderHash: ByteArray, merkleRoot: ByteArray, timestamp: Long, bits: Long, nonce: Long): ByteArray {
        return BitcoinOutput()
                .writeInt(version)
                .write(previousBlockHeaderHash)
                .write(merkleRoot)
                .writeUnsignedInt(timestamp)
                .writeUnsignedInt(bits)
                .writeUnsignedInt(nonce)
                .toByteArray()
    }
}
