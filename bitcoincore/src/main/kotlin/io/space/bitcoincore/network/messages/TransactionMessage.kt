package io.space.bitcoincore.network.messages

import io.space.bitcoincore.extensions.toReversedHex
import io.space.bitcoincore.io.BitcoinInput
import io.space.bitcoincore.serializers.TransactionSerializer
import io.space.bitcoincore.storage.FullTransaction
import java.io.ByteArrayInputStream

class TransactionMessage(var transaction: FullTransaction, val size: Int) : IMessage {
    override fun toString(): String {
        return "TransactionMessage(${transaction.header.hash.toReversedHex()})"
    }
}

class TransactionMessageParser : IMessageParser {
    override val command: String = "tx"

    override fun parseMessage(payload: ByteArray): IMessage {
        BitcoinInput(ByteArrayInputStream(payload)).use { input ->
            val transaction = TransactionSerializer.deserialize(input)
            return TransactionMessage(transaction, payload.size)
        }
    }
}

class TransactionMessageSerializer : IMessageSerializer {
    override val command: String = "tx"

    override fun serialize(message: IMessage): ByteArray? {
        if (message !is TransactionMessage) {
            return null
        }

        return TransactionSerializer.serialize(message.transaction)
    }
}
