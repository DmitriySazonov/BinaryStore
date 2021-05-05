package com.example.binaryjson.generator.adapter

import com.binarystore.InjectType
import com.binarystore.adapter.BinaryAdapter
import com.binarystore.adapter.BinaryAdapterProvider
import com.example.binaryjson.generator.*
import com.example.binaryjson.generator.adapter.types.TypeCodeGenerator
import com.example.binaryjson.generator.adapter.types.TypeCodeGeneratorFactory
import com.squareup.javapoet.*
import java.util.*
import javax.annotation.Nonnull
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier
import kotlin.collections.HashMap
import kotlin.collections.HashSet

private const val ADAPTER_SUFFIX = "BinaryAdapter"
private const val ID_FIELD_NAME = "ID"
private const val BUFFER_NAME = "byteBuffer"

private const val GET_KEY_METHOD = "key"

private const val VALUE = "value"
private const val ADAPTER_PROVIDER_FIELD = "adapterProvider"
private const val VERSION_FIELD = "versionId"
private const val ADAPTER_FIELD_SUFFIX = "Adapter"

class AdapterBuilder(
        private val env: ProcessingEnvironment
) {

    private val context = object : TypeCodeGenerator.Context {
        val uniqueAdapterTypes = HashSet<ClassName>()
        private val valueName = "var"
        private var valueOrder = 0

        override fun getOrCreateAdapterFieldFor(type: ClassName): String {
            uniqueAdapterTypes.add(type)
            return adapterFiledName(type)
        }

        override fun generateAdapterByKeyExpression(
                keyExpression: InlineExpression,
                properties: PropertiesName?
        ): String {
            return "${ADAPTER_PROVIDER_FIELD}.${
                AdapterProviderGeneratorHelper
                        .invoke_getAdapterByKey(keyExpression, properties)
            }"
        }

        override fun generateAdapterForClassExpression(
                classExpression: InlineExpression,
                properties: PropertiesName?
        ): String {
            return "${ADAPTER_PROVIDER_FIELD}.${
                AdapterProviderGeneratorHelper
                        .invoke_getAdapterForClass(classExpression, properties)
            }"
        }

        override fun getAdapterTypeNameFor(className: ClassName): TypeName {
            return ParameterizedTypeName.get(ClassName.get(BinaryAdapter::class.java), className)
        }

        override fun generateValName(): String {
            return "$valueName${valueOrder++}"
        }
    }

    fun build(metadata: TypeMetadata): JavaFile {
        val fields = metadata.fields
        val valueType = TypeName.get(metadata.element.asType())
        val (classPrefix, packageName) = getPrefixAndPackage(metadata.element)
        val className = metadata.element.simpleName.toString()
        val adapterName = makeAdapterName(classPrefix, className)
        val adapterClassName = ClassName.get(packageName, adapterName)

        val typeSpec: TypeSpec = TypeSpec.classBuilder(adapterName).apply {
            metadata.id.generateStaticFiled(ID_FIELD_NAME, this)
            addModifiers(Modifier.PUBLIC)
            addModifiers(Modifier.FINAL)
            addOriginatingElement(metadata.element)
            addSuperinterface(getAdapterInterfaceType(metadata.element))

            val sizeMethod = generateSizeMethod(VALUE, valueType, generateSizeCode(fields))
            val serializeMethod = generateSerializeMethod(VALUE, valueType, BUFFER_NAME,
                    generateSerializeCode(metadata))
            val deserializeMethod = generateDeserializeMethod(valueType, BUFFER_NAME,
                    generateDeserializeCode(metadata))

            val uniqueTypes = context.uniqueAdapterTypes

            addField(generateVersionField(metadata.versionId))
            addField(BinaryAdapterProvider::class.java, ADAPTER_PROVIDER_FIELD,
                    Modifier.PRIVATE, Modifier.FINAL)
            addFields(generateAdapterField(uniqueTypes))

            addMethod(generateConstructor(uniqueTypes))
            addMethod(sizeMethod)
            addMethod(serializeMethod)
            addMethod(deserializeMethod)
            addMethod(generateGetKeyMethod(metadata))

            AdapterFactoryBuilder.inject(this, adapterClassName, metadata, ID_FIELD_NAME)
        }.build()
        return JavaFile.builder(packageName, typeSpec).build()
    }

    private fun makeAdapterName(prefix: String, type: String): String {
        return "$prefix$type$ADAPTER_SUFFIX"
    }

    private fun generateVersionField(versionId: Int): FieldSpec {
        return FieldSpec.builder(TypeName.INT,
                VERSION_FIELD,
                Modifier.PRIVATE,
                Modifier.FINAL
        ).apply {
            initializer("$versionId")
        }.build()
    }

    private fun generateConstructor(fields: Collection<ClassName>): MethodSpec {
        val providerName = "provider"
        return MethodSpec.constructorBuilder().apply {
            addException(Exception::class.java)
            addParameter(BinaryAdapterProvider::class.java, providerName)

            fields.forEach {
                addStatement("${adapterFiledName(it)} = ${providerName}." +
                        AdapterProviderGeneratorHelper
                                .invoke_getAdapterForClass(
                                        classExpression = InlineExpression("\$T.class"),
                                        properties = null
                                ), it)
            }
            addStatement("this.$ADAPTER_PROVIDER_FIELD = $providerName")
        }.build()
    }

    private fun generateAdapterField(fields: Collection<ClassName>): List<FieldSpec> {
        return fields.map { type ->
            val name = adapterFiledName(type)
            val adapterType = ParameterizedTypeName.get(
                    ClassName.get(BinaryAdapter::class.java),
                    type
            )
            FieldSpec.builder(adapterType, name, Modifier.PRIVATE, Modifier.FINAL).build()
        }
    }

    private fun generateSizeCode(fields: List<Field>): CodeBlock {
        val accumulator = "accumulator_${context.generateValName()}"
        return CodeBlock.builder().apply {
            addStatement("int $accumulator = 0")
            val parts = fields.map {
                TypeCodeGeneratorFactory.create(it.typeMeta).generateGetSize(
                        value = ValueName("${VALUE}.${it.name}"),
                        properties = null,
                        accumulator = AccumulatorName(accumulator),
                        context = context,
                        builder = this
                )
            }.flatten()
            add("return $accumulator + ")
            var primitiveSum = 0
            parts.forEach {
                when (it) {
                    is TypeCodeGenerator.SizePart.Constant -> primitiveSum += it.size
                    is TypeCodeGenerator.SizePart.Expression -> add("${it.expression}\n + ")
                }
            }
            add("$primitiveSum;\n")
        }.build()
    }

    private fun generateSerializeCode(metadata: TypeMetadata): CodeBlock {
        return CodeBlock.builder().apply {
            metadata.fields.map {
                TypeCodeGeneratorFactory.create(it.typeMeta).generateSerialize(
                        value = ValueName("${VALUE}.${it.name}"),
                        buffer = BufferName(BUFFER_NAME),
                        properties = null,
                        context = context,
                        builder = this
                )
            }
        }.build()
    }

    private fun generateDeserializeCode(metadata: TypeMetadata): CodeBlock {
        val fieldToValue = HashMap<Field, String>()
        return CodeBlock.builder().apply {
            metadata.fields.forEach {
                fieldToValue[it] = TypeCodeGeneratorFactory.create(it.typeMeta)
                        .generateDeserialize(
                                buffer = BufferName(BUFFER_NAME),
                                properties = null,
                                context = context,
                                builder = this
                        ).expression
            }
            when (metadata.injectType) {
                InjectType.ASSIGNMENT -> generateDeserializeAssignmentCode(fieldToValue, metadata)
                InjectType.CONSTRUCTOR -> generateDeserializeConstructorCode(fieldToValue, metadata)
            }
        }.build()
    }

    private fun CodeBlock.Builder.generateDeserializeAssignmentCode(
            fieldToValue: Map<Field, String>,
            metadata: TypeMetadata,
    ) {
        addStatement("\$T $VALUE = new \$T()", metadata.element, metadata.element)
        metadata.fields.forEach {
            addStatement("${VALUE}.${it.name} = ${fieldToValue[it]}")
        }
        add("return $VALUE;")
    }

    private fun CodeBlock.Builder.generateDeserializeConstructorCode(
            fieldToValue: Map<Field, String>,
            metadata: TypeMetadata,
    ) {
        add("return new \$T(\n", metadata.element)
        metadata.fields.map {
            CodeBlock.of(fieldToValue[it])
        }.also {
            add(CodeBlock.join(it, ",\n"))
        }
        add("\n);\n")
    }

    private fun generateGetKeyMethod(metadata: TypeMetadata): MethodSpec {
        return adapterMethod(GET_KEY_METHOD) {
            addAnnotation(Nonnull::class.java)
            addStatement("return $ID_FIELD_NAME")
            returns(metadata.id.keyClass)
        }
    }

    private fun adapterFiledName(type: ClassName): String {
        return "${type.simpleName()}${ADAPTER_FIELD_SUFFIX}"
                .decapitalize(Locale.getDefault())
    }
}
