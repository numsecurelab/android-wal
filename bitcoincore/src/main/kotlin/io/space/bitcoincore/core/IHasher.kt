package io.space.bitcoincore.core

interface IHasher {
    fun hash(data: ByteArray) : ByteArray
}
