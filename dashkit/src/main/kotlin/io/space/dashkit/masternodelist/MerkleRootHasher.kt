package io.space.dashkit.masternodelist

import io.space.bitcoincore.core.IHasher
import io.space.bitcoincore.utils.HashUtils
import io.space.dashkit.IMerkleHasher

class MerkleRootHasher: IHasher, IMerkleHasher {

    override fun hash(data: ByteArray): ByteArray {
        return HashUtils.doubleSha256(data)
    }

    override fun hash(first: ByteArray, second: ByteArray): ByteArray {
        return HashUtils.doubleSha256(first + second)
    }
}
