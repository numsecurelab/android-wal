package io.space.bitcoincore.managers

import io.space.bitcoincore.core.IStorage
import io.space.bitcoincore.core.publicKey
import io.space.bitcoincore.models.PublicKey
import io.space.bitcoincore.storage.PublicKeyWithUsedState
import io.space.hdwalletkit.HDWallet

class PublicKeyManager(private val storage: IStorage, private val hdWallet: HDWallet, private val restoreKeyConverter: RestoreKeyConverterChain)
    : IBloomFilterProvider {

    // IBloomFilterProvider

    override var bloomFilterManager: BloomFilterManager? = null

    override fun getBloomFilterElements(): List<ByteArray> {
        val elements = mutableListOf<ByteArray>()

        for (publicKey in storage.getPublicKeys()) {
            elements.addAll(restoreKeyConverter.bloomFilterElements(publicKey))
        }

        return elements
    }

    @Throws
    fun receivePublicKey(): PublicKey {
        return getPublicKey(true)
    }

    @Throws
    fun changePublicKey(): PublicKey {
        return getPublicKey(external = false)
    }

    fun getPublicKeyByPath(path: String): PublicKey {
        val parts = path.split("/").map { it.toInt() }

        return hdWallet.publicKey(parts[0], parts[2], parts[1] == 1)
    }

    fun fillGap() {
        val lastUsedAccount = storage.getPublicKeysUsed().map { it.account }.max()

        val requiredAccountsCount = if (lastUsedAccount != null) {
            lastUsedAccount + 1 + 1 //  One because account starts from 0, One because we must have n+1 accounts
        } else {
            1
        }

        repeat(requiredAccountsCount) { account ->
            fillGap(account, true)
            fillGap(account, false)
        }

        bloomFilterManager?.regenerateBloomFilter()
    }

    fun addKeys(keys: List<PublicKey>) {
        if (keys.isEmpty()) return

        storage.savePublicKeys(keys)
    }

    fun gapShifts(): Boolean {
        val publicKeys = storage.getPublicKeysWithUsedState()
        val lastAccount = publicKeys.map { it.publicKey.account }.max() ?: return false

        for (i in 0..lastAccount) {
            if (gapKeysCount(publicKeys.filter { it.publicKey.account == i && it.publicKey.external }) < hdWallet.gapLimit) {
                return true
            }

            if (gapKeysCount(publicKeys.filter { it.publicKey.account == i && !it.publicKey.external }) < hdWallet.gapLimit) {
                return true
            }
        }

        return false
    }

    private fun fillGap(account: Int, external: Boolean) {
        val publicKeys = storage.getPublicKeysWithUsedState().filter { it.publicKey.account == account && it.publicKey.external == external }
        val keysCount = gapKeysCount(publicKeys)
        val keys = mutableListOf<PublicKey>()

        if (keysCount < hdWallet.gapLimit) {
            val lastIndex = publicKeys.maxBy { it.publicKey.index }?.publicKey?.index ?: -1

            for (i in 1..hdWallet.gapLimit - keysCount) {
                val publicKey = hdWallet.publicKey(account, lastIndex + i, external)
                keys.add(publicKey)
            }
        }

        addKeys(keys)
    }

    private fun gapKeysCount(publicKeys: List<PublicKeyWithUsedState>): Int {
        val lastUsedKey = publicKeys.filter { it.used }.sortedBy { it.publicKey.index }.lastOrNull()

        return when (lastUsedKey) {
            null -> publicKeys.size
            else -> publicKeys.filter { it.publicKey.index > lastUsedKey.publicKey.index }.size
        }
    }

    @Throws
    private fun getPublicKey(external: Boolean): PublicKey {
        return storage.getPublicKeysUnused()
                .filter { it.account == 0 && it.external == external }
                .sortedWith(compareBy { it.index })
                .firstOrNull() ?: throw Error.NoUnusedPublicKey
    }

    companion object {
        fun create(storage: IStorage, hdWallet: HDWallet, restoreKeyConverter: RestoreKeyConverterChain): PublicKeyManager {
            val addressManager = PublicKeyManager(storage, hdWallet, restoreKeyConverter)
            addressManager.fillGap()
            return addressManager
        }
    }

    object Error {
        object NoUnusedPublicKey : Exception()
    }

}
