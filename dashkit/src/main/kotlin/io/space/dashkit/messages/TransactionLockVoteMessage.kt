package io.space.dashkit.messages

import io.space.bitcoincore.extensions.toReversedHex
import io.space.bitcoincore.io.BitcoinInput
import io.space.bitcoincore.io.BitcoinOutput
import io.space.bitcoincore.network.messages.IMessage
import io.space.bitcoincore.network.messages.IMessageParser
import io.space.bitcoincore.utils.HashUtils
import java.io.ByteArrayInputStream

class TransactionLockVoteMessage(
        var txHash: ByteArray,
        var outpoint: Outpoint,
        var outpointMasternode: Outpoint,
        var quorumModifierHash: ByteArray,
        var masternodeProTxHash: ByteArray,
        var vchMasternodeSignature: ByteArray,
        var hash: ByteArray) : IMessage {

    override fun toString(): String {
        return "TransactionLockVoteMessage(hash=${hash.toReversedHex()}, txHash=${txHash.toReversedHex()})"
    }

}

class Outpoint(val txHash: ByteArray, val vout: Long) {
    constructor(input: BitcoinInput) : this(input.readBytes(32), input.readUnsignedInt())
}

class TransactionLockVoteMessageParser : IMessageParser {
    override val command: String = "txlvote"

    override fun parseMessage(payload: ByteArray): IMessage {
        BitcoinInput(ByteArrayInputStream(payload)).use { bitcoinInput ->
            val txHash = bitcoinInput.readBytes(32)
            val outpoint = Outpoint(bitcoinInput)
            val outpointMasternode = Outpoint(bitcoinInput)
            val quorumModifierHash = bitcoinInput.readBytes(32)
            val masternodeProTxHash = bitcoinInput.readBytes(32)
            val signatureLength = bitcoinInput.readVarInt()
            val vchMasternodeSignature = ByteArray(signatureLength.toInt())
            bitcoinInput.readFully(vchMasternodeSignature)

            val hashPayload = BitcoinOutput()
                    .write(txHash)
                    .write(outpoint.txHash)
                    .writeUnsignedInt(outpoint.vout)
                    .write(outpointMasternode.txHash)
                    .writeUnsignedInt(outpointMasternode.vout)
                    .write(quorumModifierHash)
                    .write(masternodeProTxHash)
                    .toByteArray()

            val hash = HashUtils.doubleSha256(hashPayload)

            return TransactionLockVoteMessage(txHash, outpoint, outpointMasternode, quorumModifierHash, masternodeProTxHash, vchMasternodeSignature, hash)
        }
    }
}
