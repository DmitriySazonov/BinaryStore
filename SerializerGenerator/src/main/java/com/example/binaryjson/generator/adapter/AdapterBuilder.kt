package com.example.binaryjson.generator.adapter

import com.binarystore.InjectType
import com.binarystore.adapter.BinaryAdapter
import com.binarystore.adapter.BinaryAdapterProvider
import com.binarystore.buffer.ByteBuffer
import com.binarystore.dependency.SingletonProperties
import com.example.binaryjson.generator.*
import com.example.binaryjson.generator.adapter.types.TypeCodeGenerator
import com.example.binaryjson.generator.adapter.types.TypeCodeGeneratorFactory
import com.squareup.javapoet.*
import javax.annotation.Nonnull
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier

private const val ADAPTER_SUFFIX = "BinaryAdapter"
private const val ID_FIELD_NAME = "ID"
private const val BUFFER_NAME = "byteBuffer"

private const val GET_KEY_METHOD = "key"

private const val VALUE = "value"
private const val ADAPTER_PROVIDER_FIELD = "adapterProvider"
private const val VERSION_FIELD = "versionId"
private const val ADAPTER_FIELD_SUFFIX = "Adapter"

private const val CUSTOM_PROPERTY_SUFFIX = "Properties"

class AdapterBuilder(
        private val env: ProcessingEnvironment
) {

    private class GeneratedField(
            val filedSpec: FieldSpec,
            val initializeBlock: CodeBlock
    )

    fun build(metadata: TypeMetadata): JavaFile {
        val context = AdapterBuilderContext(ADAPTER_FIELD_SUFFIX, ADAPTER_PROVIDER_FIELD)
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

            val customProperties = generateCustomPropertiesFields(context, fields)
            val sizeMethod = generateSizeMethod(VALUE, valueType, generateSizeCode(context, fields))
            val serializeMethod = generateSerializeMethod(VALUE, valueType, BUFFER_NAME,
                    generateSerializeCode(context, metadata))
            val deserializeMethod = generateDeserializeMethod(valueType, BUFFER_NAME,
                    generateDeserializeCode(context, metadata))

            val uniqueTypes = context.uniqueAdapterTypes

            addField(generateVersionField(metadata.versionId))
            addField(BinaryAdapterProvider::class.java, ADAPTER_PROVIDER_FIELD,
                    Modifier.PRIVATE, Modifier.FINAL)
            addFields(customProperties.map { it.filedSpec })
            addFields(generateAdapterField(context, uniqueTypes))

            addMethod(generateConstructor(context, uniqueTypes,
                    customProperties.map { it.initializeBlock }))
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

    private fun generateConstructor(
            context: AdapterBuilderContext,
            fields: Collection<StaticAdapterEntry>,
            customFieldsInitializes: List<CodeBlock>
    ): MethodSpec {
        val providerName = "provider"
        return MethodSpec.constructorBuilder().apply {
            addException(Exception::class.java)
            addParameter(BinaryAdapterProvider::class.java, providerName)

            customFieldsInitializes.forEach { addCode(it) }

            fields.forEach {
                val adapterForClass = AdapterProviderGeneratorHelper.invoke_getAdapterForClass(
                        classExpression = InlineExpression("\$T.class"),
                        properties = it.propertiesName
                )
                addStatement("${context.adapterFiledName(it)} = $providerName.$adapterForClass",
                        it.className)
            }
            addStatement("this.$ADAPTER_PROVIDER_FIELD = $providerName")
        }.build()
    }

    private fun generateAdapterField(
            context: AdapterBuilderContext,
            fields: Collection<StaticAdapterEntry>
    ): List<FieldSpec> {
        return fields.map { entry ->
            val name = context.adapterFiledName(entry)
            val adapterType = ParameterizedTypeName.get(
                    ClassName.get(BinaryAdapter::class.java),
                    entry.className
            )
            FieldSpec.builder(adapterType, name, Modifier.PRIVATE, Modifier.FINAL).build()
        }
    }

    private fun getPropertyForField(context: AdapterBuilderContext, field: Field): PropertiesName? {
        return context.fieldToProperty[field.name]
    }

    private fun generateCustomPropertiesFields(
            context: AdapterBuilderContext,
            fields: List<Field>
    ): List<GeneratedField> {
        return fields.filter {
            !it.properties.isNullOrEmpty()
        }.mapNotNull {
            val properties = it.properties ?: return@mapNotNull null
            val propertiesName = PropertiesName("${it.name}$CUSTOM_PROPERTY_SUFFIX")
            context.fieldToProperty[it.name] = propertiesName
            if (properties.size == 1) {
                generateSingleProperty(propertiesName, properties.first())
            } else {
                generateMultiProperty(propertiesName, properties)
            }
        }
    }

    private fun generateMultiProperty(name: PropertiesName, properties: List<TypeName>): GeneratedField {
        val addPropertyInvoke = MultiPropertiesGeneratorHelper
                .invoke_addNewProperty(InlineExpression("new \$T()"))
        val fieldType = MultiPropertiesGeneratorHelper.type
        val spec = FieldSpec.builder(fieldType, name.name,
                Modifier.PRIVATE, Modifier.FINAL).build()

        val initializer = CodeBlock.builder().apply {
            addStatement("${name.name} = new \$T()", fieldType)
            properties.forEach {
                addStatement("${name.name}.$addPropertyInvoke", it)
            }
        }.build()
        return GeneratedField(spec, initializer)
    }

    private fun generateSingleProperty(name: PropertiesName, property: TypeName): GeneratedField {
        val spec = FieldSpec.builder(SingletonProperties::class.java, name.name,
                Modifier.PRIVATE, Modifier.FINAL).build()
        val initializer = CodeBlock.of("${name.name} = new \$T(new \$T()); \n",
                SingletonProperties::class.java, property)
        return GeneratedField(spec, initializer)
    }

    private fun generateSizeCode(context: AdapterBuilderContext, fields: List<Field>): CodeBlock {
        val accumulator = "accumulator_${context.getUniqueValName()}"
        return CodeBlock.builder().apply {
            addStatement("int $accumulator = ${ByteBuffer.INTEGER_BYTES}") // size for version
            val parts = fields.map {
                TypeCodeGeneratorFactory.create(it.typeMeta).generateGetSize(
                        value = ValueName("${VALUE}.${it.name}"),
                        properties = getPropertyForField(context, it),
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

    private fun generateSerializeCode(context: AdapterBuilderContext, metadata: TypeMetadata): CodeBlock {
        return CodeBlock.builder().apply {
            addStatement(BufferGeneratorHelper.invoke_write(BufferName(BUFFER_NAME),
                    ValueName(VERSION_FIELD)))
            metadata.fields.map {
                TypeCodeGeneratorFactory.create(it.typeMeta).generateSerialize(
                        value = ValueName("${VALUE}.${it.name}"),
                        buffer = BufferName(BUFFER_NAME),
                        properties = getPropertyForField(context, it),
                        context = context,
                        builder = this
                )
            }
        }.build()
    }

    private fun generateDeserializeCode(context: AdapterBuilderContext, metadata: TypeMetadata): CodeBlock {
        val fieldToValue = HashMap<Field, String>()
        return CodeBlock.builder().apply {
            val readInt = BufferGeneratorHelper
                    .invoke_readByType(BufferName(BUFFER_NAME), TypeName.INT)
            val readVersion = context.getUniqueValName("version")
            addStatement("final int $readVersion = $readInt")
            metadata.fields.forEach {
                fieldToValue[it] = TypeCodeGeneratorFactory.create(it.typeMeta)
                        .generateDeserialize(
                                buffer = BufferName(BUFFER_NAME),
                                properties = getPropertyForField(context, it),
                                context = context,
                                builder = this
                        ).expression
            }
            when (metadata.injectType) {
                InjectType.ASSIGNMENT -> generateDeserializeAssignmentCode(fieldToValue, metadata)
                InjectType.CONSTRUCTOR -> generateDeserializeConstructorCode(fieldToValue, metadata)
                InjectType.AUTO -> generateDeserializeAutoCode(fieldToValue, metadata)
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
        val constructor = metadata.findFullMatchConstructor()
                ?: throw BadConstructorException.FullMachError(metadata.type)
        generateNewObjectCode(VALUE, metadata.type, constructor,
                fieldToValue.mapKeys { it.key.toConstructorParam() })
        addStatement("return $VALUE")
    }

    private fun CodeBlock.Builder.generateDeserializeAutoCode(
            fieldToValue: Map<Field, String>,
            metadata: TypeMetadata
    ) {
        val constructor = metadata.findMostAppropriateConstructor()
                ?: throw BadConstructorException.NotExistAppropriateConstructor(metadata.type)
        val mutableFieldsToValue = fieldToValue.mapKeys {
            it.key.toConstructorParam()
        }.toMutableMap()
        generateNewObjectCode(VALUE, metadata.type, constructor, mutableFieldsToValue)
        constructor.params.forEach(mutableFieldsToValue::remove)

        mutableFieldsToValue.forEach {
            addStatement("${VALUE}.${it.key.name} = ${it.value}")
        }
        add("return $VALUE;")
    }

    private fun CodeBlock.Builder.generateNewObjectCode(
            valueName: String,
            typeName: TypeName,
            constructor: Constructor,
            fieldToValue: Map<Constructor.Param, String>
    ) {
        if (constructor.params.isEmpty()) {
            addStatement("\$T $valueName = new \$T()", typeName, typeName)
            return
        }
        add("\$T $valueName = new \$T(\n", typeName, typeName)
        constructor.params.map {
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

    private fun Field.toConstructorParam(): Constructor.Param {
        return Constructor.Param(name, typeMeta.type)
    }
}
