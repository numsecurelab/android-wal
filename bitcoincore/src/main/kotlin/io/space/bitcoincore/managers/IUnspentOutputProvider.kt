package io.space.bitcoincore.managers

import io.space.bitcoincore.storage.UnspentOutput

interface IUnspentOutputProvider {
    fun getSpendableUtxo(): List<UnspentOutput>
}
