package io.space.dashkit.core

import io.space.bitcoincore.core.IHasher
import io.space.bitcoincore.utils.HashUtils

class SingleSha256Hasher : IHasher {
    override fun hash(data: ByteArray): ByteArray {
        return HashUtils.sha256(data)
    }
}