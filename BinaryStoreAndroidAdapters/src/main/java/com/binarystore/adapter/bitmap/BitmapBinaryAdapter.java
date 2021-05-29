package com.binarystore.adapter.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.AndroidDefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.buffer.ByteBuffer;
import com.binarystore.buffer.StaticByteBuffer;
import com.binarystore.dependency.PropertiesUtils;

import java.io.OutputStream;
import java.util.Objects;

import javax.annotation.Nonnull;

public final class BitmapBinaryAdapter extends AbstractBinaryAdapter<Bitmap> {

    public static final Key.Byte KEY = AndroidDefaultAdapters.BITMAP;
    public static final Factory factory = new Factory();

    private final BitmapSettings settings;

    BitmapBinaryAdapter(BitmapSettings settings) {
        this.settings = settings;
    }

    @Nonnull
    @Override
    public Key.Byte key() {
        return KEY;
    }

    @Nonnull
    @Override
    public Bitmap deserialize(@Nonnull ByteBuffer byteBuffer) {
        final int size = byteBuffer.readInt();
        final int start = byteBuffer.getOffset();
        final StaticByteBuffer staticBuffer = byteBuffer.getSubBuffer(start, start + size);
        byteBuffer.moveOffset(staticBuffer.getSize());
        return Objects.requireNonNull(BitmapFactory.decodeByteArray(
                staticBuffer.bytes, staticBuffer.getAbsoluteOffset(), size
        ));
    }

    @Override
    public int getSize(@Nonnull Bitmap value) {
        return value.getAllocationByteCount() + ByteBuffer.INTEGER_BYTES;
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Bitmap value) {
        final int size = value.getAllocationByteCount();
        byteBuffer.write(size);
        final OutputStream stream = byteBuffer.reserveOutputStream(value.getAllocationByteCount());
        value.compress(settings.compressFormat, settings.quality, stream);
    }

    private static final class Factory implements AdapterFactory<Bitmap, BitmapBinaryAdapter> {

        @Override
        public Key.Byte adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public BitmapBinaryAdapter create(@Nonnull Context context) {
            final BitmapSettings settings = PropertiesUtils.getOrDefault(context,
                    BitmapSettings.class, BitmapSettings.defaultSettings);
            return new BitmapBinaryAdapter(settings);
        }
    }
}
