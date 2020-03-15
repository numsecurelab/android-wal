package io.space.bitcoincore.managers

import io.space.bitcoincore.core.IConnectionManagerListener
import io.space.bitcoincore.network.peer.PeerGroup

class SyncManager(private val peerGroup: PeerGroup, private val initialSyncer: InitialSyncer)
    : InitialSyncer.Listener, IConnectionManagerListener {

    fun start() {
        initialSyncer.sync()
    }

    fun stop() {
        initialSyncer.stop()
        peerGroup.stop()
    }

    //
    // ConnectionManager Listener
    //

    override fun onConnectionChange(isConnected: Boolean) {
        if (isConnected) {
            start()
        } else {
            stop()
        }
    }

    //
    // InitialSyncer Listener
    //

    override fun onSyncingFinished() {
        initialSyncer.stop()
        peerGroup.start()
    }
}
