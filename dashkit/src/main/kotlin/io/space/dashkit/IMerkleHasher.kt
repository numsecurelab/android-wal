package io.space.dashkit

interface IMerkleHasher {
    fun hash(first: ByteArray, second: ByteArray) : ByteArray
}