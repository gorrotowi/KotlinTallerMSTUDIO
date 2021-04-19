package com.mobilestudio.myapplication.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.zxing.integration.android.IntentIntegrator

class QrCodeScannerContract : ActivityResultContract<Void?, String>() {

    override fun createIntent(context: Context, input: Void?): Intent {
        return IntentIntegrator(context as Activity).apply {
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            setPrompt("Read Qr Code")
            setBeepEnabled(true)
        }.createScanIntent()
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String {
        val result = IntentIntegrator.parseActivityResult(resultCode, intent)
        return if (result!=null && result.contents!=null){
            result.contents
        } else {
            ""
        }
    }
}