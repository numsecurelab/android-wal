package io.space.dashkit.masternodelist

import io.space.dashkit.models.Masternode

class MasternodeListMerkleRootCalculator(val masternodeMerkleRootCreator: MerkleRootCreator) {

    fun calculateMerkleRoot(sortedMasternodes: List<Masternode>): ByteArray? {
        return masternodeMerkleRootCreator.create(sortedMasternodes.map { it.hash })
    }

}
