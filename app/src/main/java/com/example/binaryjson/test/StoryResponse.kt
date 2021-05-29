package com.example.binaryjson.test

import android.graphics.Bitmap
import com.binarystore.adapter.bitmap.BitmapSettings
import com.binarystore.annotation.Persistable
import com.binarystore.annotation.ProvideProperties
import com.binarystore.dependency.Property

@Persistable(id = "StoryResponse")
class StoryResponse(
        @JvmField val id: String,
        @JvmField val type: String,
        @JvmField val title: String,
        @JvmField val ttl: Long,
        @JvmField val items: List<Channel>,
        @JvmField val rid: String,
        @JvmField val item_id: String,
        @JvmField val item_type: String
) {

    @Persistable(id = "Logos")
    class Logos(
            @JvmField val small: String,
            @JvmField val normal: String
    )

    @Persistable(id = "Source")
    class Source(
            @JvmField val id: String,
            @JvmField val type: String,
            @JvmField val title: String,
            @JvmField val logo: String,
            @JvmField val logos: Logos,
            @JvmField val logoBackgroundColor: String,
            @JvmField val feedLink: String,
            @JvmField val feedApiLink: String,
            @JvmField val subscribers: Int,
            @JvmField val isVerified: Boolean,
            @JvmField val subscriptionForbidden: Boolean
    )

    @Persistable(id = "Previews")
    class Previews(
            @JvmField val small: String,
            @JvmField val normal: String,
            @JvmField val big: String
    )

    @Persistable(id = "Story")
    class Story(
            @JvmField val id: String,
            @JvmField val publicationTime: Long,
            @JvmField val item_id: String,
            @JvmField val item_type: String,
            @JvmField val ttl: Long,
            @JvmField val preview: String,
            @JvmField val previews: Previews,
            @JvmField val video: String,
            @JvmField val status: String,
//            @field:ProvideProperties(properties = [BitmapSettingsProperty::class])
            @JvmField val blurredPreview: Bitmap?
    )

    @Persistable(id = "Channel")
    class Channel(
            @JvmField val id: String,
            @JvmField val source: Source,
            @JvmField val isSubscribed: Boolean,
            @JvmField val stories: List<Story>
    )

    class BitmapSettingsProperty : Property<BitmapSettings> {
        override fun name(): String? = null

        override fun typeClass(): Class<BitmapSettings> = BitmapSettings::class.java

        override fun provide(): BitmapSettings =
                BitmapSettings(Bitmap.CompressFormat.PNG, 100)
    }
}
