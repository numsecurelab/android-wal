package io.space.bitcoincore.network.messages

import io.space.bitcoincore.io.BitcoinInput
import io.space.bitcoincore.models.NetworkAddress
import java.io.ByteArrayInputStream

class AddrMessage(var addresses: List<NetworkAddress>) : IMessage {
    override fun toString(): String {
        return "AddrMessage(count=${addresses.size})"
    }
}

class AddrMessageParser : IMessageParser {
    override val command = "addr"

    override fun parseMessage(payload: ByteArray): IMessage {
        BitcoinInput(ByteArrayInputStream(payload)).use { input ->
            val count = input.readVarInt() // do not store count

            val addresses = List(count.toInt()) {
                NetworkAddress(input, false)
            }

            return AddrMessage(addresses)
        }
    }
}
