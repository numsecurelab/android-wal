package io.space.dashkit.messages

import io.space.bitcoincore.extensions.toReversedHex
import io.space.bitcoincore.io.BitcoinInput
import io.space.bitcoincore.network.messages.IMessage
import io.space.bitcoincore.network.messages.IMessageParser
import io.space.bitcoincore.serializers.TransactionSerializer
import io.space.bitcoincore.storage.FullTransaction
import java.io.ByteArrayInputStream

class TransactionLockMessage(var transaction: FullTransaction) : IMessage {
    override fun toString(): String {
        return "TransactionLockMessage(${transaction.header.hash.toReversedHex()})"
    }
}

class TransactionLockMessageParser : IMessageParser {
    override val command: String = "ix"

    override fun parseMessage(payload: ByteArray): IMessage {
        BitcoinInput(ByteArrayInputStream(payload)).use { input ->
            val transaction = TransactionSerializer.deserialize(input)
            return TransactionLockMessage(transaction)
        }
    }
}
