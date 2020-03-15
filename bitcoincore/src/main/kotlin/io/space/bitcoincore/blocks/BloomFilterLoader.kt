package io.space.bitcoincore.blocks

import io.space.bitcoincore.crypto.BloomFilter
import io.space.bitcoincore.managers.BloomFilterManager
import io.space.bitcoincore.network.peer.Peer
import io.space.bitcoincore.network.peer.PeerGroup
import io.space.bitcoincore.network.peer.PeerManager

class BloomFilterLoader(private val bloomFilterManager: BloomFilterManager, private val peerManager: PeerManager)
    : PeerGroup.Listener, BloomFilterManager.Listener {

    override fun onPeerConnect(peer: Peer) {
        bloomFilterManager.bloomFilter?.let {
            peer.filterLoad(it)
        }
    }

    override fun onFilterUpdated(bloomFilter: BloomFilter) {
        peerManager.connected().forEach {
            it.filterLoad(bloomFilter)
        }
    }
}
