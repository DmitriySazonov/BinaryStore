package com.example.binaryjson;

import com.binarystore.Array;
import com.binarystore.Persistable;

import java.util.Map;

@Persistable(id = 3)
public class TestClassJava {

    @Persistable(id = 4)
    static class InnerClass {
        final Map<String, String> map;
        final String[][][][] array;
        @Array(even = true)
        final int[][][][] arrayInt;

        InnerClass(Map<String, String> map, String[][][][] array, int[][][][] arrayInt) {
            this.map = map;
            this.array = array;
            this.arrayInt = arrayInt;
        }
    }

    final String testPublicField;
    final boolean testPrivateFiled;
    final Boolean testPrivateFiledBox;
    final short testShortField;

    TestClassJava(
            String testPublicField,
            boolean testPrivateFiled,
            Boolean testPrivateFiledBox,
            short testShortField
    ) {
        this.testPublicField = testPublicField;
        this.testPrivateFiled = testPrivateFiled;
        this.testPrivateFiledBox = testPrivateFiledBox;
        this.testShortField = testShortField;
    }
}
