package com.samadtch.inspired.common.exceptions

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

//TODO: improve crashlytics error reporting
actual fun sendCrashlytics(e: Exception) {
    Firebase.crashlytics.recordException(e)
}