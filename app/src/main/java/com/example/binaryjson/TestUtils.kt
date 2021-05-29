package com.example.binaryjson

import com.binarystore.AdaptersRegistrator
import com.binarystore.adapter.AndroidBasicBinaryAdapters
import com.binarystore.adapter.BasicBinaryAdapters
import com.binarystore.adapter.collection.CollectionSettings
import com.binarystore.adapter.map.MapSettings
import com.binarystore.dependency.MultiProperties
import com.binarystore.manager.BinaryAdapterManager

fun createDefaultBinaryAdapterManager(): BinaryAdapterManager {
    return BinaryAdapterManager(
            MultiProperties().apply {
                addProperty(MapSettings.ThrowExceptionSettingProperty())
                addProperty(CollectionSettings.ThrowExceptionSettingProperty())
            }
    ).apply {
        AndroidBasicBinaryAdapters.registerInto(this)
        BasicBinaryAdapters.registerInto(this)
        AdaptersRegistrator.registerInto(this)
    }
}