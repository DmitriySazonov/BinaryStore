package com.binarystore.adapter;

public @interface DefaultAdapters {
    Key.Int BOOLEAN = new Key.Int(IdProvider.sId(1));
    Key.Int BYTE = new Key.Int(IdProvider.sId(2));
    Key.Int SHORT = new Key.Int(IdProvider.sId(3));
    Key.Int INT = new Key.Int(IdProvider.sId(4));
    Key.Int LONG = new Key.Int(IdProvider.sId(5));
    Key.Int FLOAT = new Key.Int(IdProvider.sId(6));
    Key.Int DOUBLE = new Key.Int(IdProvider.sId(7));
    Key.Int STRING = new Key.Int(IdProvider.sId(8));

    Key.Int MAP = new Key.Int(IdProvider.sId(9));
    Key.Int TREE_MAP = new Key.Int(IdProvider.sId(10));
    Key.Int HASH_MAP = new Key.Int(IdProvider.sId(11));
}
