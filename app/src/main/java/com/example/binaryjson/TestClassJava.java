package com.example.binaryjson;

import com.binarystore.annotation.Array;
import com.binarystore.InjectType;
import com.binarystore.annotation.Persistable;

import java.util.Map;

@Persistable(id = "TestClassJava")
public class TestClassJava {

    @Persistable(id = "InnerClass", inject = InjectType.AUTO)
    static class InnerClass extends TestClassJava {
        Map<String, String> map;
        String[][][][] array;
        @Array(even = true)
        int[][][][] arrayInt;

        InnerClass() {
            super("", false,
                    false, (byte) 2, new TestNestedClass());
        }

        public InnerClass(
                String testPublicField,
                boolean testPrivateFiled,
                Boolean testPrivateFiledBox,
                short testShortField,
                TestNestedClass testNestedClass
        ) {
            super(testPublicField, testPrivateFiled, testPrivateFiledBox, testShortField, testNestedClass);
        }
    }

    @Persistable(id = "TestNestedClass", inject = InjectType.ASSIGNMENT)
    static class TestNestedClass {
        String testPublicField = "skdfblf";
    }

    final String testPublicField;
    final boolean testPrivateFiled;
    final Boolean testPrivateFiledBox;
    final short testShortField;
    final TestNestedClass testNestedClass;

    TestClassJava(
            String testPublicField,
            boolean testPrivateFiled,
            Boolean testPrivateFiledBox,
            short testShortField,
            TestNestedClass testNestedClass
    ) {
        this.testPublicField = testPublicField;
        this.testPrivateFiled = testPrivateFiled;
        this.testPrivateFiledBox = testPrivateFiledBox;
        this.testShortField = testShortField;
        this.testNestedClass = testNestedClass;
    }

    public TestClassJava() {
        this("", false, false,
                (byte) 2, new TestNestedClass());
    }
}
