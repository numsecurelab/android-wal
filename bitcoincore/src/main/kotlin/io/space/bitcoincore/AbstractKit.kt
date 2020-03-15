package io.space.bitcoincore

import io.space.bitcoincore.core.IPluginData
import io.space.bitcoincore.models.BitcoinPaymentData
import io.space.bitcoincore.models.PublicKey
import io.space.bitcoincore.network.Network
import io.space.bitcoincore.storage.FullTransaction
import io.space.bitcoincore.storage.UnspentOutput
import io.space.bitcoincore.transactions.scripts.ScriptType

abstract class AbstractKit {

    protected abstract var bitcoinCore: BitcoinCore
    protected abstract var network: Network

    val balance
        get() = bitcoinCore.balance

    val lastBlockInfo
        get() = bitcoinCore.lastBlockInfo

    val networkName: String
        get() = network.javaClass.simpleName

    fun start() {
        bitcoinCore.start()
    }

    fun stop() {
        bitcoinCore.stop()
    }

    fun refresh() {
        bitcoinCore.refresh()
    }

    fun fee(value: Long, address: String? = null, senderPay: Boolean = true, feeRate: Int, pluginData: Map<Byte, IPluginData> = mapOf()): Long {
        return bitcoinCore.fee(value, address, senderPay, feeRate, pluginData)
    }

    fun send(address: String, value: Long, senderPay: Boolean = true, feeRate: Int, pluginData: Map<Byte, IPluginData> = mapOf()): FullTransaction {
        return bitcoinCore.send(address, value, senderPay, feeRate, pluginData)
    }

    fun send(hash: ByteArray, scriptType: ScriptType, value: Long, senderPay: Boolean = true, feeRate: Int): FullTransaction {
        return bitcoinCore.send(hash, scriptType, value, senderPay, feeRate)
    }

    fun redeem(unspentOutput: UnspentOutput, address: String, feeRate: Int): FullTransaction {
        return bitcoinCore.redeem(unspentOutput, address, feeRate)
    }

    fun receiveAddress(): String {
        return bitcoinCore.receiveAddress()
    }

    fun receivePublicKey(): PublicKey {
        return bitcoinCore.receivePublicKey()
    }

    fun changePublicKey(): PublicKey {
        return bitcoinCore.changePublicKey()
    }

    fun validateAddress(address: String, pluginData: Map<Byte, IPluginData>) {
        bitcoinCore.validateAddress(address, pluginData)
    }

    fun parsePaymentAddress(paymentAddress: String): BitcoinPaymentData {
        return bitcoinCore.parsePaymentAddress(paymentAddress)
    }

    fun showDebugInfo() {
        bitcoinCore.showDebugInfo()
    }

    fun statusInfo(): Map<String, Any> {
        return bitcoinCore.statusInfo()
    }

    fun getPublicKeyByPath(path: String): PublicKey {
        return bitcoinCore.getPublicKeyByPath(path)
    }

    fun watchTransaction(filter: TransactionFilter, listener: WatchedTransactionManager.Listener) {
        bitcoinCore.watchTransaction(filter, listener)
    }

    fun maximumSpendableValue(address: String?, feeRate: Int, pluginData: Map<Byte, IPluginData>): Long {
        return bitcoinCore.maximumSpendableValue(address, feeRate, pluginData)
    }

    fun minimumSpendableValue(address: String?): Int {
        return bitcoinCore.minimumSpendableValue(address)
    }

    fun maximumSpendLimit(pluginData: Map<Byte, IPluginData>): Long? {
        return bitcoinCore.maximumSpendLimit(pluginData)
    }
}
