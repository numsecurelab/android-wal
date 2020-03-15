package io.space.bitcoincore.storage

import android.arch.persistence.room.Embedded
import io.space.bitcoincore.models.*
import io.space.bitcoincore.serializers.TransactionSerializer
import io.space.bitcoincore.utils.HashUtils

class BlockHeader(
        val version: Int,
        val previousBlockHeaderHash: ByteArray,
        val merkleRoot: ByteArray,
        val timestamp: Long,
        val bits: Long,
        val nonce: Long,
        val hash: ByteArray)

class FullTransaction(val header: Transaction, val inputs: List<TransactionInput>, val outputs: List<TransactionOutput>) {

    init {
        if (header.hash.isEmpty()) {
            header.hash = HashUtils.doubleSha256(TransactionSerializer.serialize(this, withWitness = false))
        }

        inputs.forEach {
            it.transactionHash = header.hash
        }
        outputs.forEach {
            it.transactionHash = header.hash
        }
    }

}

class InputToSign(
        val input: TransactionInput,
        val previousOutput: TransactionOutput,
        val previousOutputPublicKey: PublicKey)

class TransactionWithBlock(
        @Embedded val transaction: Transaction,
        @Embedded val block: Block?)

class PublicKeyWithUsedState(
        @Embedded val publicKey: PublicKey,
        val usedCount: Int) {

    val used: Boolean
        get() = usedCount > 0
}

class PreviousOutput(val publicKeyPath: String?, val value: Long)

class InputWithPreviousOutput(
        @Embedded val input: TransactionInput,
        @Embedded val previousOutput: PreviousOutput?)

class UnspentOutput(
        @Embedded val output: TransactionOutput,
        @Embedded val publicKey: PublicKey,
        @Embedded val transaction: Transaction,
        @Embedded val block: Block?)

class FullTransactionInfo(
        val block: Block?,
        val header: Transaction,
        val inputs: List<InputWithPreviousOutput>,
        val outputs: List<TransactionOutput>)
