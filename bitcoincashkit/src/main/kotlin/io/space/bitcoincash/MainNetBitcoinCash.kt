package io.space.bitcoincash

import io.space.bitcoincore.network.Network
import io.space.bitcoincore.transactions.scripts.Sighash
import kotlin.experimental.or

class MainNetBitcoinCash : Network() {

    override var port: Int = 8333

    override var magic: Long = 0xe8f3e1e3L
    override var bip32HeaderPub: Int = 0x0488b21e
    override var bip32HeaderPriv: Int = 0x0488ade4
    override var addressVersion: Int = 0
    override var addressSegwitHrp: String = "bitcoincash"
    override var addressScriptVersion: Int = 5
    override var coinType: Int = 0

    override val maxBlockSize = 32 * 1024 * 1024
    override val dustRelayTxFee = 1000 // https://github.com/Bitcoin-ABC/bitcoin-abc/blob/master/src/policy/policy.h#L78
    override val sigHashForked = true
    override val sigHashValue = Sighash.FORKID or Sighash.ALL

    override var dnsSeeds = listOf(
            "seed.bitcoinabc.org",                  // Bitcoin ABC seeder
            "seed-abc.bitcoinforks.org",            // bitcoinforks seeders
            "btccash-seeder.bitcoinunlimited.info", // BU backed seeder
            "seed.bitprim.org",                     // Bitprim
            "seed.deadalnix.me",                    // Amaury SÉCHET
            "seeder.criptolayer.net"                // criptolayer.net
    )
}
