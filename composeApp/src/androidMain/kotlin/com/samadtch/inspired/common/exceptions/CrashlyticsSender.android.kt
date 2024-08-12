package com.samadtch.inspired.common.exceptions

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

actual fun sendCrashlytics(e: Exception) {
    Firebase.crashlytics.recordException(e)
}