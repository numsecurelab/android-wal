package io.space.litecoinkit

import com.lambdaworks.crypto.SCrypt
import io.space.bitcoincore.core.IHasher

class ScryptHasher : IHasher {

    override fun hash(data: ByteArray): ByteArray {
        return try {
            SCrypt.scrypt(data, data, 1024, 1, 1, 32).reversedArray()
        } catch (e: Exception) {
            byteArrayOf()
        }
    }

}
