package io.space.bitcoincore.managers

import io.space.bitcoincore.models.BlockHash
import io.space.bitcoincore.models.PublicKey
import io.reactivex.Single

interface IBlockDiscovery {
    fun discoverBlockHashes(account: Int, external: Boolean): Single<Pair<List<PublicKey>, List<BlockHash>>>
}
