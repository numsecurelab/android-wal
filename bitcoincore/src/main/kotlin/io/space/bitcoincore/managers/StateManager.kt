package io.space.bitcoincore.managers

import io.space.bitcoincore.core.IStorage

class StateManager(private val storage: IStorage, private val restoreFromApi: Boolean) {

    var restored: Boolean
        get() {
            if (!restoreFromApi) {
                return true
            }

            return storage.initialRestored ?: false
        }
        set(value) {
            storage.setInitialRestored(value)
        }
}
