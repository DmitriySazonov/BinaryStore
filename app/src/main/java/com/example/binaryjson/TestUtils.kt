package com.example.binaryjson

import com.binarystore.AdaptersRegistrator
import com.binarystore.manager.BinaryAdapterManager
import com.binarystore.adapter.BasicBinaryAdapters

fun createDefaultBinaryAdapterManager(): BinaryAdapterManager {
    return BinaryAdapterManager().apply {
        BasicBinaryAdapters.registerInto(this)
        AdaptersRegistrator.registerInto(this)
    }
}