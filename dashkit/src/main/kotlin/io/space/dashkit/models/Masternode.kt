package io.space.dashkit.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import io.space.bitcoincore.io.BitcoinInput
import io.space.bitcoincore.io.BitcoinOutput
import io.space.bitcoincore.utils.HashUtils

@Entity
class Masternode() : Comparable<Masternode> {

    @PrimaryKey
    var proRegTxHash = byteArrayOf()
    var confirmedHash = byteArrayOf()
    var ipAddress = byteArrayOf()
    var port = 0
    var pubKeyOperator = byteArrayOf()
    var keyIDVoting = byteArrayOf()
    var isValid = false

    var hash = byteArrayOf()

    constructor(input: BitcoinInput) : this() {
        proRegTxHash = input.readBytes(32)
        confirmedHash = input.readBytes(32)
        ipAddress = input.readBytes(16)
        port = input.readUnsignedShort()
        pubKeyOperator = input.readBytes(48)
        keyIDVoting = input.readBytes(20)
        isValid = input.read() != 0

        val hashPayload = BitcoinOutput()
                .write(proRegTxHash)
                .write(confirmedHash)
                .write(ipAddress)
                .writeUnsignedShort(port)
                .write(pubKeyOperator)
                .write(keyIDVoting)
                .writeByte(if (isValid) 1 else 0)
                .toByteArray()

        hash = HashUtils.doubleSha256(hashPayload)
    }

    override fun compareTo(other: Masternode): Int {

        for (i in 0 until 32) {
            val b1: Int = proRegTxHash[i].toInt() and 0xff
            val b2: Int = other.proRegTxHash[i].toInt() and 0xff

            val res = b1.compareTo(b2)
            if (res != 0) return res
        }

        return 0
    }

    override fun equals(other: Any?) = when (other) {
        is Masternode -> proRegTxHash.contentEquals(other.proRegTxHash)
        else -> false
    }

    override fun hashCode(): Int {
        return proRegTxHash.contentHashCode()
    }

}
