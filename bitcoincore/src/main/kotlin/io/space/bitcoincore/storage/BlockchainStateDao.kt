package io.space.bitcoincore.storage

import android.arch.persistence.room.*
import io.space.bitcoincore.models.BlockchainState

@Dao
interface BlockchainStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(state: BlockchainState)

    @Query("SELECT * FROM BlockchainState LIMIT 1")
    fun getState(): BlockchainState?

    @Delete
    fun delete(state: BlockchainState)
}
