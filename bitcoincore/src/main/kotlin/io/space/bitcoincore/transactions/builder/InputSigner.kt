package io.space.bitcoincore.transactions.builder

import io.space.bitcoincore.models.Transaction
import io.space.bitcoincore.models.TransactionOutput
import io.space.bitcoincore.network.Network
import io.space.bitcoincore.serializers.TransactionSerializer
import io.space.bitcoincore.storage.InputToSign
import io.space.bitcoincore.transactions.scripts.ScriptType
import io.space.hdwalletkit.HDWallet

class InputSigner(private val hdWallet: HDWallet, val network: Network) {

    fun sigScriptData(transaction: Transaction, inputsToSign: List<InputToSign>, outputs: List<TransactionOutput>, index: Int): List<ByteArray> {

        val input = inputsToSign[index]
        val prevOutput = input.previousOutput
        val publicKey = input.previousOutputPublicKey

        val privateKey = checkNotNull(hdWallet.privateKey(publicKey.account, publicKey.index, publicKey.external)) {
            throw Error.NoPrivateKey()
        }

        val txContent = TransactionSerializer.serializeForSignature(transaction, inputsToSign, outputs, index, prevOutput.scriptType.isWitness || network.sigHashForked) + byteArrayOf(network.sigHashValue, 0, 0, 0)
        val signature = privateKey.createSignature(txContent) + network.sigHashValue

        return when {
            prevOutput.scriptType == ScriptType.P2PK -> listOf(signature)
            else -> listOf(signature, publicKey.publicKey)
        }
    }

    open class Error : Exception() {
        class NoPrivateKey : Error()
        class NoPreviousOutput : Error()
        class NoPreviousOutputAddress : Error()
    }
}
