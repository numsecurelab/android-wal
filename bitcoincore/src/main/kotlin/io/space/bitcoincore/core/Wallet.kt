package io.space.bitcoincore.core

import io.space.bitcoincore.models.PublicKey
import io.space.hdwalletkit.HDWallet

class Wallet(private val hdWallet: HDWallet) {

    val gapLimit = hdWallet.gapLimit

    fun publicKey(account: Int, index: Int, external: Boolean): PublicKey {
        val hdPubKey = hdWallet.hdPublicKey(account, index, external)
        return PublicKey(account, index, hdPubKey.external, hdPubKey.publicKey, hdPubKey.publicKeyHash)
    }

}
