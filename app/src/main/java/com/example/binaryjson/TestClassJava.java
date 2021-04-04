package com.example.binaryjson;

import android.os.Parcel;
import android.os.Parcelable;

import com.binarystore.Persistable;

import java.util.Map;

@Persistable(id = 3)
public class TestClassJava {

    @Persistable(id = 4)
    static class InnerClass {
        final Map<String, String> map;
//        final String[] array;

        InnerClass(Map<String, String> map) {
            this.map = map;
//            this.array = array;
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
