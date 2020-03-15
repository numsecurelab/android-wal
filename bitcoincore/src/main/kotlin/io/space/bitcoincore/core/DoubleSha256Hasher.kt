package io.space.bitcoincore.core

import io.space.bitcoincore.utils.HashUtils

class DoubleSha256Hasher : IHasher {
    override fun hash(data: ByteArray): ByteArray {
        return HashUtils.doubleSha256(data)
    }
}
