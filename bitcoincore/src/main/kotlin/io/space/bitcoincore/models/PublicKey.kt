package io.space.bitcoincore.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import io.space.bitcoincore.core.IStorage
import io.space.bitcoincore.transactions.scripts.OpCodes
import io.space.bitcoincore.utils.Utils

@Entity(primaryKeys = ["path"],
        indices = [
            Index("publicKey"),
            Index("publicKeyHash"),
            Index("scriptHashP2WPKH")
        ])

class PublicKey {
    var path: String = ""

    var account = 0
    @ColumnInfo(name = "address_index")
    var index = 0
    var external = true

    var publicKeyHash = byteArrayOf()
    var publicKey = byteArrayOf()
    var scriptHashP2WPKH = byteArrayOf()

    fun used(storage: IStorage): Boolean {
        return storage.getOutputsOfPublicKey(this).isNotEmpty()
    }

    constructor()
    constructor(account: Int, index: Int, external: Boolean, publicKey: ByteArray, publicKeyHash: ByteArray) : this() {
        this.path = "$account/${if (external) 1 else 0}/$index"
        this.account = account
        this.index = index
        this.external = external
        this.publicKey = publicKey
        this.publicKeyHash = publicKeyHash

        val version = 0
        val redeemScript = OpCodes.push(version) + OpCodes.push(this.publicKeyHash)
        this.scriptHashP2WPKH = Utils.sha256Hash160(redeemScript)
    }
}
