package com.binarystore.buffer;

final class InnerByteArrayOutputStream {

    InnerByteArrayOutputStream() {

    }

    /*public synchronized void write(int var1) {
        this.ensureCapacity(this.count + 1);
        this.buf[this.count] = (byte)var1;
        ++this.count;
    }

    public synchronized void write(byte[] var1, int var2, int var3) {
        if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 - var1.length <= 0) {
            this.ensureCapacity(this.count + var3);
            System.arraycopy(var1, var2, this.buf, this.count, var3);
            this.count += var3;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }*/

}
