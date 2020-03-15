package io.space.dashkit.managers

import io.space.bitcoincore.BitcoinCore
import io.space.bitcoincore.blocks.IPeerSyncListener
import io.space.bitcoincore.blocks.InitialBlockDownload
import io.space.bitcoincore.extensions.toReversedByteArray
import io.space.bitcoincore.network.peer.IPeerTaskHandler
import io.space.bitcoincore.network.peer.Peer
import io.space.bitcoincore.network.peer.PeerGroup
import io.space.bitcoincore.network.peer.task.PeerTask
import io.space.dashkit.tasks.PeerTaskFactory
import io.space.dashkit.tasks.RequestMasternodeListDiffTask
import java.util.concurrent.Executors

class MasternodeListSyncer(
        private val bitcoinCore: BitcoinCore,
        private val peerTaskFactory: PeerTaskFactory,
        private val masternodeListManager: MasternodeListManager,
        private val initialBlockDownload: InitialBlockDownload)
    : IPeerTaskHandler, IPeerSyncListener, PeerGroup.Listener {

    @Volatile
    private var workingPeer: Peer? = null
    private val peersQueue = Executors.newSingleThreadExecutor()

    override fun onPeerSynced(peer: Peer) {
        assignNextSyncPeer()
    }

    override fun onPeerDisconnect(peer: Peer, e: Exception?) {
        if (peer == workingPeer) {
            workingPeer = null

            assignNextSyncPeer()
        }
    }

    private fun assignNextSyncPeer() {
        peersQueue.execute {
            if (workingPeer == null) {
                bitcoinCore.lastBlockInfo?.let { lastBlockInfo ->
                    initialBlockDownload.syncedPeers.firstOrNull()?.let { syncedPeer ->
                        val blockHash = lastBlockInfo.headerHash.toReversedByteArray()
                        val baseBlockHash = masternodeListManager.baseBlockHash

                        if (!blockHash.contentEquals(baseBlockHash)) {
                            val task = peerTaskFactory.createRequestMasternodeListDiffTask(baseBlockHash, blockHash)
                            syncedPeer.addTask(task)

                            workingPeer = syncedPeer
                        }
                    }
                }
            }
        }
    }


    override fun handleCompletedTask(peer: Peer, task: PeerTask): Boolean {
        return when (task) {
            is RequestMasternodeListDiffTask -> {
                task.masternodeListDiffMessage?.let { masternodeListDiffMessage ->
                    masternodeListManager.updateList(masternodeListDiffMessage)
                    workingPeer = null
                }
                true
            }
            else -> false
        }
    }

}
