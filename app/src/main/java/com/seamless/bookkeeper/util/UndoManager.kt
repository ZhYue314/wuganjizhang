package com.seamless.bookkeeper.util

import com.seamless.bookkeeper.data.local.entity.TransactionEntity
import com.seamless.bookkeeper.data.local.dao.TransactionDao
import java.util.concurrent.ConcurrentLinkedDeque
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UndoManager @Inject constructor(
    private val transactionDao: TransactionDao
) {
    companion object {
        private const val MAX_DEPTH = 50
    }

    private val undoStack = ConcurrentLinkedDeque<UndoSnapshot>()

    fun saveSnapshot(snapshot: UndoSnapshot) {
        cleanupExpired()
        if (undoStack.size >= MAX_DEPTH) undoStack.pollFirst()
        undoStack.addLast(snapshot)
    }

    suspend fun undo(): Boolean {
        cleanupExpired()
        val snapshot = undoStack.pollLast() ?: return false
        return try {
            when (snapshot) {
                is UndoSnapshot.TransactionDelete -> transactionDao.insert(snapshot.transaction)
                is UndoSnapshot.TransactionEdit -> transactionDao.update(snapshot.original)
            }
            true
        } catch (e: Exception) { false }
    }

    private fun cleanupExpired() {
        val now = System.currentTimeMillis()
        while (undoStack.isNotEmpty() && (now - undoStack.first.timestamp) > 10_000L) {
            undoStack.pollFirst()
        }
    }
}

sealed class UndoSnapshot(val timestamp: Long = System.currentTimeMillis()) {
    data class TransactionDelete(val transaction: TransactionEntity) : UndoSnapshot()
    data class TransactionEdit(val original: TransactionEntity) : UndoSnapshot()
}
