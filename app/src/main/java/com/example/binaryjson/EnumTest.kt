package com.example.binaryjson

import com.binarystore.adapter.AbstractBinaryAdapter
import com.binarystore.adapter.AdapterFactory
import com.binarystore.adapter.Key
import com.binarystore.buffer.ByteBuffer

enum class EnumTest {
    TEST_0, TEST_1, TEST_2, TEST_3, TEST_4, TEST_5, TEST_6, TEST_7, TEST_8, TEST_9, TEST_10,
    TEST_11, TEST_12, TEST_13, TEST_14, TEST_15, TEST_16, TEST_17, TEST_18, TEST_19, TEST_20,
    TEST_21, TEST_22, TEST_23, TEST_24, TEST_25, TEST_26, TEST_27, TEST_28, TEST_29, TEST_30,
    TEST_31, TEST_32, TEST_33, TEST_34, TEST_35, TEST_36, TEST_37, TEST_38, TEST_39, TEST_40,
    TEST_41, TEST_42, TEST_43, TEST_44, TEST_45, TEST_46, TEST_47, TEST_48, TEST_49, TEST_50,
    TEST_51, TEST_52, TEST_53, TEST_54, TEST_55, TEST_56, TEST_57, TEST_58, TEST_59, TEST_60,
    TEST_61, TEST_62, TEST_63, TEST_64, TEST_65, TEST_66, TEST_67, TEST_68, TEST_69, TEST_70,
    TEST_71, TEST_72, TEST_73, TEST_74, TEST_75, TEST_76, TEST_77, TEST_78, TEST_79, TEST_80,
    TEST_81, TEST_82, TEST_83, TEST_84, TEST_85, TEST_86, TEST_87, TEST_88, TEST_89, TEST_90,
    TEST_91, TEST_92, TEST_93, TEST_94, TEST_95, TEST_96, TEST_97, TEST_98, TEST_99
}

class EnumTestBinaryAdapter(context: AdapterFactory.Context) : AbstractBinaryAdapter<EnumTest>() {

    class Factory : AdapterFactory<EnumTest, EnumTestBinaryAdapter> {
        override fun adapterKey(): Key<*> {
            return Key.String("EnumTest")
        }

        override fun create(context: AdapterFactory.Context): EnumTestBinaryAdapter {
            return EnumTestBinaryAdapter(context)
        }

    }
/*
    private val stringAdapter = context.adapterProvider
            .getAdapterForClass(String::class.java, null)!!*/

    override fun key(): Key<*> {
        return Key.String("EnumTest")
    }

    override fun getSize(value: EnumTest): Int {
        return ByteBuffer.INTEGER_BYTES
    }

    override fun serialize(byteBuffer: ByteBuffer, value: EnumTest) {
        byteBuffer.write(value.ordinal)
    }

    override fun deserialize(byteBuffer: ByteBuffer): EnumTest {
        return EnumTest.values()[byteBuffer.readInt()]
    }
}