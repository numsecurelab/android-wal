package io.space.bitcoincore.storage

import android.arch.persistence.room.TypeConverter
import io.space.bitcoincore.extensions.hexToByteArray
import io.space.bitcoincore.extensions.toHexString

class WitnessConverter {

    @TypeConverter
    fun fromWitness(list: List<ByteArray>): String {
        return list.joinToString(", ") {
            it.toHexString()
        }
    }

    @TypeConverter
    fun toWitness(data: String): List<ByteArray> = when {
        data.isEmpty() -> listOf()
        else -> data.split(", ").map {
            it.hexToByteArray()
        }
    }
}
