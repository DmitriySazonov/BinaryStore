package com.example.binaryjson;

public class SerializationUtils {

    public static void test() {
        String[] object1 = new String[]{"", ""};
        Object[] object2 = new Object[]{object1, object1};
        Object[][] object3 = (Object[][]) object2;


        System.out.println("Hello");
    }

    public static void putString(DynamicByteBuffer buffer, String value) {
        final int len = value.length();
        final byte[] bytes = new byte[len * 2];
        char curChar;
        for (int i = 0, j = 0; i < len; i++) {
            curChar = value.charAt(i);
            bytes[j++] = (byte) (curChar);
            bytes[j++] = (byte) (curChar >>> 8);
        }
        buffer.write(len);
        buffer.write(bytes);
    }

    public static String getString(DynamicByteBuffer buffer) {
        final int length = buffer.readInt();
        final byte[] bytes = new byte[length * 2];
        buffer.readBytes(bytes);

        final char[] chars = new char[length];
        for (int i = 0, j = 0; i < length; i++) {
            chars[i] = (char) (((bytes[j++] & 0xFF)) | ((bytes[j++] & 0xFF) << 8));
        }
        return new String(chars);
    }
}
