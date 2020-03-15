package io.space.dashkit.instantsend

import io.space.bitcoincore.io.BitcoinOutput
import io.space.bitcoincore.utils.HashUtils
import io.space.dashkit.DashKitErrors
import io.space.dashkit.managers.QuorumListManager
import io.space.dashkit.messages.ISLockMessage
import io.space.dashkit.models.QuorumType

class InstantSendLockValidator(
        private val quorumListManager: QuorumListManager,
        private val bls: BLS
) {

    @Throws
    fun validate(islock: ISLockMessage) {
        // 01. Select quorum
        val quorum = quorumListManager.getQuorum(QuorumType.LLMQ_50_60, islock.requestId)

        // 02. Make signId data to verify signature
        val signIdPayload = BitcoinOutput()
                .writeByte(quorum.type)
                .write(quorum.quorumHash)
                .write(islock.requestId)
                .write(islock.txHash)
                .toByteArray()

        val signId = HashUtils.doubleSha256(signIdPayload)

        // 03. Verify signature by BLS
        if (!bls.verifySignature(quorum.quorumPublicKey, islock.sign, signId)) {
            throw DashKitErrors.ISLockValidation.SignatureNotValid()
        }
    }
}
