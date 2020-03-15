package io.space.bitcoincore.core

import io.space.bitcoincore.blocks.IBlockchainDataListener
import io.space.bitcoincore.extensions.toReversedHex
import io.space.bitcoincore.managers.UnspentOutputProvider
import io.space.bitcoincore.models.*
import io.space.bitcoincore.storage.FullTransactionInfo
import io.space.bitcoincore.storage.TransactionWithBlock
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class DataProvider(private val storage: IStorage, private val unspentOutputProvider: UnspentOutputProvider, private val transactionInfoConverter: ITransactionInfoConverter)
    : IBlockchainDataListener {

    interface Listener {
        fun onTransactionsUpdate(inserted: List<TransactionInfo>, updated: List<TransactionInfo>)
        fun onTransactionsDelete(hashes: List<String>)
        fun onBalanceUpdate(balance: BalanceInfo)
        fun onLastBlockInfoUpdate(blockInfo: BlockInfo)
    }

    var listener: Listener? = null
    private val balanceUpdateSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val balanceSubjectDisposable: Disposable

    //  Getters
    var balance: BalanceInfo = unspentOutputProvider.getBalance()
        private set(value) {
            if (value != field) {
                field = value
                listener?.onBalanceUpdate(field)
            }
        }

    var lastBlockInfo: BlockInfo?
        private set

    init {
        lastBlockInfo = storage.lastBlock()?.let {
            blockInfo(it)
        }

        balanceSubjectDisposable = balanceUpdateSubject.debounce(500, TimeUnit.MILLISECONDS)
                .subscribe {
                    balance = unspentOutputProvider.getBalance()
                }
    }

    override fun onBlockInsert(block: Block) {
        if (block.height > lastBlockInfo?.height ?: 0) {
            val blockInfo = blockInfo(block)

            lastBlockInfo = blockInfo
            listener?.onLastBlockInfoUpdate(blockInfo)
            balanceUpdateSubject.onNext(true)
        }
    }

    override fun onTransactionsUpdate(inserted: List<Transaction>, updated: List<Transaction>, block: Block?) {
        listener?.onTransactionsUpdate(
                storage.getFullTransactionInfo(inserted.map { TransactionWithBlock(it, block) }).map { transactionInfoConverter.transactionInfo(it) },
                storage.getFullTransactionInfo(updated.map { TransactionWithBlock(it, block) }).map { transactionInfoConverter.transactionInfo(it) }
        )

        balanceUpdateSubject.onNext(true)
    }

    override fun onTransactionsDelete(hashes: List<String>) {
        listener?.onTransactionsDelete(hashes)
        balanceUpdateSubject.onNext(true)
    }

    fun clear() {
        balanceSubjectDisposable.dispose()
    }

    fun transactions(fromUid: String?, limit: Int? = null): Single<List<TransactionInfo>> =
            Single.create { emitter ->
                var results = listOf<FullTransactionInfo>()
                if (fromUid != null) {
                    storage.getValidOrInvalidTransaction(fromUid)?.let {
                        results = storage.getFullTransactionInfo(it, limit)
                    }
                } else {
                    results = storage.getFullTransactionInfo(null, limit)
                }

                emitter.onSuccess(results.map { transactionInfoConverter.transactionInfo(it) })
            }

    private fun blockInfo(block: Block) = BlockInfo(
            block.headerHash.toReversedHex(),
            block.height,
            block.timestamp)

}