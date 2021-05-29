package com.binarystore.adapter.bitmap;

import android.graphics.Bitmap;

import javax.annotation.Nonnull;

public final class BitmapSettings {

    final static BitmapSettings defaultSettings = new BitmapSettings(
            Bitmap.CompressFormat.JPEG, 100
    );

    final int quality;
    final Bitmap.CompressFormat compressFormat;

    public BitmapSettings(
            @Nonnull final Bitmap.CompressFormat compressFormat,
            final int quality
    ) {
        this.compressFormat = compressFormat;
        this.quality = Math.max(Math.min(quality, 100), 0);
    }
}
