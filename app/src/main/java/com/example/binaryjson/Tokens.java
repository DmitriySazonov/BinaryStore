package com.example.binaryjson;

public class Tokens {

    private static byte TOKEN_ID = 1;

    public static final byte JSON_OBJECT = TOKEN_ID++;
    public static final byte JSON_ARRAY = TOKEN_ID++;
    public static final byte BOOLEAN = TOKEN_ID++;
    public static final byte BYTE = TOKEN_ID++;
    public static final byte INT = TOKEN_ID++;
    public static final byte LONG = TOKEN_ID++;
    public static final byte FLOAT = TOKEN_ID++;
    public static final byte DOUBLE = TOKEN_ID++;
    public static final byte STRING = TOKEN_ID++;
    public static final byte NULL = TOKEN_ID++;
    public static final byte SET = TOKEN_ID++;
    public static final byte MAP = TOKEN_ID++;
}
