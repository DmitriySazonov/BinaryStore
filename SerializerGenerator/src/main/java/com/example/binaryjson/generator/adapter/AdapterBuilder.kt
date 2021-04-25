package com.example.binaryjson.generator.adapter

import com.binarystore.InjectType
import com.binarystore.adapter.BinaryAdapter
import com.binarystore.adapter.BinaryAdapterProvider
import com.binarystore.meta.MetadataStore
import com.example.binaryjson.generator.AdapterProviderGeneratorHelper
import com.example.binaryjson.generator.Field
import com.example.binaryjson.generator.TypeMetadata
import com.example.binaryjson.generator.adapter.types.TypeCodeGenerator
import com.example.binaryjson.generator.adapter.types.TypeCodeGeneratorFactory
import com.squareup.javapoet.*
import java.util.*
import javax.lang.model.element.Modifier
import kotlin.collections.HashMap
import kotlin.collections.HashSet

private const val ADAPTER_SUFFIX = "BinaryAdapter"
private const val ID_FIELD_NAME = "ID"
private const val BUFFER_NAME = "byteBuffer"

private const val GET_KEY_METHOD = "key"

private const val VALUE = "value"
private const val META_STORE_FIELD = "metadataStore"
private const val ADAPTER_PROVIDER_FIELD = "adapterProvider"
private const val VERSION_FIELD = "versionId"
private const val ADAPTER_FIELD_SUFFIX = "Adapter"

class AdapterBuilder {

    private val context = object : TypeCodeGenerator.Context {
        val uniqueAdapterTypes = HashSet<ClassName>()
        private val valueName = "var"
        private var valueOrder = 0

        override fun getOrCreateAdapterFieldFor(type: ClassName): String {
            uniqueAdapterTypes.add(type)
            return adapterFiledName(type)
        }

        override fun generateAdapterByKeyExpression(keyExpression: String): String {
            return "${ADAPTER_PROVIDER_FIELD}.${
                AdapterProviderGeneratorHelper
                        .invoke_getAdapterByKey(keyExpression)
            }"
        }

        override fun generateAdapterForClassExpression(classExpression: String): String {
            return "${ADAPTER_PROVIDER_FIELD}.${
                AdapterProviderGeneratorHelper
                        .invoke_getAdapterForClass(classExpression)
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
            addField(MetadataStore::class.java, META_STORE_FIELD,
                    Modifier.PRIVATE, Modifier.FINAL)
            addField(BinaryAdapterProvider::class.java, ADAPTER_PROVIDER_FIELD,
                    Modifier.PRIVATE, Modifier.FINAL)
            addFields(generateAdapterField(uniqueTypes))

            addMethod(generateConstructor(uniqueTypes))
            addMethod(sizeMethod)
            addMethod(serializeMethod)
            addMethod(deserializeMethod)
            addMethod(generateGetIdMethod(metadata))

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
        val metaStoreName = "metadataStore"
        return MethodSpec.constructorBuilder().apply {
            addException(Exception::class.java)
            addParameter(BinaryAdapterProvider::class.java, providerName)
            addParameter(MetadataStore::class.java, metaStoreName)

            fields.forEach {
                addStatement("${adapterFiledName(it)} = " +
                        "${providerName}.getAdapterForClass(\$T.class)", it)
            }
            addStatement("this.$META_STORE_FIELD = $metaStoreName")
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
        return CodeBlock.builder().apply {
            val parts = fields.map {
                TypeCodeGeneratorFactory.create(it.typeMeta)
                        .generateGetSize("${VALUE}.${it.name}", context, this)
            }.flatten()
            add("return ")
            var primitiveSum = 0
            parts.forEach {
                when (it) {
                    is TypeCodeGenerator.SizePart.Constant -> {
                        primitiveSum += it.size
                    }
                    is TypeCodeGenerator.SizePart.Expression -> add("${it.expression}\n + ")
                }
            }
            add("$primitiveSum;\n")
        }.build()
    }

    private fun generateSerializeCode(metadata: TypeMetadata): CodeBlock {
        return CodeBlock.builder().apply {
            metadata.fields.map {
                TypeCodeGeneratorFactory.create(it.typeMeta).generateSerialize("${VALUE}.${it.name}",
                        BUFFER_NAME, context, this)
            }
        }.build()
    }

    private fun generateDeserializeCode(metadata: TypeMetadata): CodeBlock {
        val fieldToValue = HashMap<Field, String>()
        return CodeBlock.builder().apply {
            metadata.fields.forEach {
                fieldToValue[it] = TypeCodeGeneratorFactory.create(it.typeMeta)
                        .generateDeserialize(BUFFER_NAME, context, this).name
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

    private fun generateGetIdMethod(metadata: TypeMetadata): MethodSpec {
        return adapterMethod(GET_KEY_METHOD) {
            addStatement("return $ID_FIELD_NAME")
            returns(metadata.id.keyClass)
        }
    }

    private fun adapterFiledName(type: ClassName): String {
        return "${type.simpleName()}${ADAPTER_FIELD_SUFFIX}"
                .decapitalize(Locale.getDefault())
    }
}
