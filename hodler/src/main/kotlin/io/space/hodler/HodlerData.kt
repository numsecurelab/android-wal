package io.space.hodler

import io.space.bitcoincore.core.IPluginData

data class HodlerData(val lockTimeInterval: LockTimeInterval) : IPluginData
