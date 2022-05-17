package com.example.bookworm.core.dataprocessing.image

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract

class ImageContract : ActivityResultContract<Intent, ActivityResult>() {
    override fun createIntent(context: Context, input: Intent): Intent {
        return input
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult {
        return intent?.getParcelableExtra("result")!!
    }
}