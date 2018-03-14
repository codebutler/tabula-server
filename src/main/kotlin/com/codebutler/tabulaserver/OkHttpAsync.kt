package com.codebutler.tabulaserver

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.experimental.suspendCoroutine

suspend fun okHttpAsync(call: Call): Response {
    return suspendCoroutine { cont ->
        call.enqueue(object : Callback {
            override fun onResponse(pdfCall: Call, pdfResponse: Response) {
                cont.resume(pdfResponse)
            }

            override fun onFailure(call: Call, e: IOException) {
                cont.resumeWithException(e)
            }
        })
    }
}