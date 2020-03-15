package io.space.dashkit.messages

import io.space.bitcoincore.io.BitcoinOutput
import io.space.bitcoincore.network.messages.IMessage
import io.space.bitcoincore.network.messages.IMessageSerializer

class GetMasternodeListDiffMessage(val baseBlockHash: ByteArray, val blockHash: ByteArray) : IMessage

class GetMasternodeListDiffMessageSerializer : IMessageSerializer {
    override val command: String = "getmnlistd"

    override fun serialize(message: IMessage): ByteArray? {
        if (message !is GetMasternodeListDiffMessage) {
            return null
        }

        return BitcoinOutput()
                .write(message.baseBlockHash)
                .write(message.blockHash)
                .toByteArray()
    }
}
