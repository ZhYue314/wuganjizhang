package com.seamless.bookkeeper.core

object Config {
    const val DUPLICATE_WINDOW_SECONDS = 30L
    const val FUSION_WINDOW_MS = 5000L
    const val DUPLICATE_AMOUNT_TOLERANCE = 0.01
    const val DUPLICATE_TIME_WINDOW_MINUTES = 5
    const val UNDO_TIMEOUT_MS = 10_000L
    const val PENDING_EXPIRY_MS = 86_400_000L
    const val MAX_IMAGES_PER_TRANSACTION = 5
    const val MAX_UNDO_DEPTH = 50

    val MONITORED_PACKAGES = setOf(
        "com.tencent.mm",
        "com.eg.android.AlipayGphone"
    )
}
