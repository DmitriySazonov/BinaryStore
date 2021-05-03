package com.binarystore.adapter;

public @interface DefaultAdapters {
    Key.Byte BOOLEAN = new Key.Byte((byte) 1);
    Key.Byte BYTE = new Key.Byte((byte) 2);
    Key.Byte SHORT = new Key.Byte((byte) 3);
    Key.Byte INT = new Key.Byte((byte) 4);
    Key.Byte LONG = new Key.Byte((byte) 5);
    Key.Byte FLOAT = new Key.Byte((byte) 6);
    Key.Byte DOUBLE = new Key.Byte((byte) 7);
    Key.Byte STRING = new Key.Byte((byte) 8);

    Key.Byte TREE_MAP = new Key.Byte((byte) 9);
    Key.Byte HASH_MAP = new Key.Byte((byte) 10);

    Key.Byte NULL = new Key.Byte((byte) 11);
}
