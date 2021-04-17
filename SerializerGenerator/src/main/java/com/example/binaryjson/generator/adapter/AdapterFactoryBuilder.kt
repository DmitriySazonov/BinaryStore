package com.example.binaryjson.generator.adapter

import com.binarystore.adapter.AdapterFactory
import com.binarystore.adapter.BinaryAdapter
import com.squareup.javapoet.*
import javax.annotation.Nonnull
import javax.lang.model.element.Modifier

object AdapterFactoryBuilder : CodeBuilder {

    private const val FACTORY_TYPE_NAME = "Factory"
    private const val FACTORY_CONTEXT_NAME = "context"

    override fun TypeSpec.Builder.build(context: CodeBuilder.Context) {
        addType(generateFactoryType(context))
    }

    private fun generateFactoryType(context: CodeBuilder.Context): TypeSpec {
        val adapterClass = ClassName.get(AdapterFactory::class.java)
        val factoryType = ParameterizedTypeName.get(adapterClass, context.metadata.type)
        return TypeSpec.classBuilder(FACTORY_TYPE_NAME).apply {
            addSuperinterface(factoryType)
            addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            addMethod(generateFactoryMethodAdapterId(context))
            addMethod(generateFactoryMethodCreate(context))
        }.build()
    }

    private fun generateFactoryMethodAdapterId(context: CodeBuilder.Context): MethodSpec {
        return MethodSpec.methodBuilder("adapterKey").apply {
            addAnnotation(Override::class.java)
            addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            context.metadata.id.generateReturnCode(this)
        }.build()
    }

    private fun generateFactoryMethodCreate(context: CodeBuilder.Context): MethodSpec {
        val adapterClass = ClassName.get(BinaryAdapter::class.java)
        return MethodSpec.methodBuilder("create").apply {
            addAnnotation(Override::class.java)
            addAnnotation(Nonnull::class.java)
            addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            addParameter(
                    ParameterSpec.builder(
                            AdapterFactory.Context::class.java,
                            FACTORY_CONTEXT_NAME
                    ).apply {
                        addAnnotation(Nonnull::class.java)
                    }.build()
            )
            val provider = "${FACTORY_CONTEXT_NAME}.provider"
            val metadataStore = "${FACTORY_CONTEXT_NAME}.metadataStore"
            addCode("return new \$T($provider, $metadataStore);", context.typeClass)
            returns(ParameterizedTypeName.get(adapterClass, context.metadata.type))
        }.build()
    }
}