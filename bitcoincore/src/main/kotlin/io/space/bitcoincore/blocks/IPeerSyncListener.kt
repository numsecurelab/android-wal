package io.space.bitcoincore.blocks

import io.space.bitcoincore.network.peer.Peer

interface IPeerSyncListener {
    fun onAllPeersSynced() = Unit
    fun onPeerSynced(peer: Peer) = Unit
}
