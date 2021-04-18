package com.example.binaryjson;

import com.binarystore.Array;
import com.binarystore.InjectType;
import com.binarystore.Persistable;

import java.util.Map;

@Persistable(id = "TestClassJava")
public class TestClassJava {

    @Persistable(id = "InnerClass", inject = InjectType.ASSIGNMENT)
    static class InnerClass extends TestClassJava {
        Map<String, String> map;
        String[][][][] array;
        @Array(even = true)
        int[][][][] arrayInt;

        InnerClass() {
            super("", false, false, (byte) 2);
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
