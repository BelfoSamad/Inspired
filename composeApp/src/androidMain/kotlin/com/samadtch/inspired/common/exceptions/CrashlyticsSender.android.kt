package com.samadtch.inspired.common.exceptions

import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.Firebase

actual fun sendCrashlytics(e: Exception) {
    Firebase.crashlytics.recordException(e)
}