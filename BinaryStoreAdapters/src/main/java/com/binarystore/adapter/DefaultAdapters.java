package com.binarystore.adapter;

public @interface DefaultAdapters {
    Key.Byte CHAR = new Key.Byte((byte) 0);
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
    Key.Byte LINKED_HASH_MAP = new Key.Byte((byte) 11);
    Key.Byte CONCURRENT_HASH_MAP = new Key.Byte((byte) 12);
    Key.Byte CONCURRENT_SKI_LIST_MAP = new Key.Byte((byte) 13);
    Key.Byte ENUM_MAP = new Key.Byte((byte) 14);
    Key.Byte MAP = new Key.Byte((byte) 15);

    Key.Byte ARRAY_LIST = new Key.Byte((byte) 20);

    Key.Byte NULL = new Key.Byte((byte) 100);
    Key.Byte CLASS = new Key.Byte((byte) 101);
    Key.Byte ENUM = new Key.Byte((byte) 102);
}
