package com.example.binaryjson.generator.adapter

import com.binarystore.adapter.BinaryAdapter
import com.binarystore.adapter.BinaryAdapterProvider
import com.binarystore.meta.MetadataStore
import com.example.binaryjson.generator.FieldMetadata
import com.example.binaryjson.generator.TypeMetadata
import com.squareup.javapoet.*
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

object AdapterBuilder {

    private const val ADAPTER_SUFFIX = "BinaryAdapter"

    fun build(metadata: TypeMetadata): JavaFile {
        val (classPrefix, packageName) = getPrefixAndPackage(metadata.element)
        val className = metadata.element.simpleName.toString()
        val adapterName = makeAdapterName(classPrefix, className)
        val uniqueTypes = findUniqueTypes(metadata.fields)
        val typeSpec: TypeSpec = TypeSpec.classBuilder(adapterName).apply {
            addModifiers(Modifier.PUBLIC)
            addModifiers(Modifier.FINAL)
            addOriginatingElement(metadata.element)
            addSuperinterface(getAdapterInterfaceType(metadata.element))

            addField(generateVersionField(metadata.versionId))
            addField(MetadataStore::class.java, META_STORE_FIELD,
                    Modifier.PRIVATE, Modifier.FINAL)
            addFields(generateAdapterField(uniqueTypes))

            addMethod(generateConstructor(uniqueTypes))
            addMethod(AdapterGetIdBuilder.generateGetIdMethod(metadata))
            addMethod(AdapterDeserializeBuilder.generateDeserializeMethod(metadata))
            addMethod(AdapterSerializeBuilder.generateSerializeMethod(metadata))
            addMethod(AdapterGetSizeBuilder.generateSizeMethod(metadata))
            addMethod(AdapterCreateArrayBuilder.generateMethod(metadata))
        }.build()
        return JavaFile.builder(packageName, typeSpec).build()
    }

    private fun getPrefixAndPackage(element: TypeElement): Pair<String, String> {
        var enclosingElement = element.enclosingElement
        var kind = enclosingElement.kind
        var prefix = ""
        while (kind != ElementKind.PACKAGE) {
            prefix += enclosingElement.simpleName
            enclosingElement = enclosingElement.enclosingElement
            kind = enclosingElement.kind
        }
        return prefix to enclosingElement.toString()
    }

    private fun getAdapterInterfaceType(element: TypeElement): TypeName {
        val typeName = TypeName.get(element.asType())
        val className = ClassName.get(BinaryAdapter::class.java)
        return ParameterizedTypeName.get(className, typeName)
    }

    private fun makeAdapterName(prefix: String, type: String): String {
        return "$prefix$type$ADAPTER_SUFFIX"
    }

    private fun findUniqueTypes(fields: List<FieldMetadata>): List<ClassName> {
        return fields.mapNotNull {
            tryGetClassName(it.type)
        }.toSet().toList()
    }

    private fun tryGetClassName(type: TypeName): ClassName? {
        if (type is ClassName)
            return type
        return when (type) {
            is ArrayTypeName -> type.componentType
            is ParameterizedTypeName -> type.rawType
            else -> null
        }?.let(::tryGetClassName)
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

    private fun generateConstructor(fields: List<ClassName>): MethodSpec {
        val providerName = "provider"
        val metaStoreName = "metadataStore"
        return MethodSpec.constructorBuilder().apply {
            addParameter(BinaryAdapterProvider::class.java, providerName)
            addParameter(MetadataStore::class.java, metaStoreName)

            fields.forEach {
                addStatement("${adapterFiledName(it)} = " +
                        "${providerName}.getAdapter(${it.simpleName()}.class)")
            }
            addStatement("this.$META_STORE_FIELD = $metaStoreName")
        }.build()
    }

    private fun generateAdapterField(fields: List<ClassName>): List<FieldSpec> {
        return fields.map { type ->
            val name = adapterFiledName(type)
            val adapterType = ParameterizedTypeName.get(
                    ClassName.get(BinaryAdapter::class.java),
                    type
            )
            FieldSpec.builder(adapterType, name, Modifier.PRIVATE, Modifier.FINAL).build()
        }
    }
}