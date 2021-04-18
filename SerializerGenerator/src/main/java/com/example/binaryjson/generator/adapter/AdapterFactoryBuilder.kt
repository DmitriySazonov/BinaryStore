package com.example.binaryjson.generator.adapter

import com.binarystore.adapter.AdapterFactory
import com.binarystore.adapter.AdapterFactoryRegister
import com.squareup.javapoet.*
import javax.annotation.Nonnull
import javax.lang.model.element.Modifier

object AdapterFactoryBuilder : AdapterCodeBuilder {

    const val REGISTER_METHOD_NAME = "registerInto"
    private const val FACTORY_TYPE_NAME = "Factory"
    private const val FACTORY_CONTEXT_NAME = "context"
    private const val REGISTER = "register"

    override fun TypeSpec.Builder.build(context: AdapterCodeBuilder.Context) {
        addType(generateFactoryType(context))
        addMethod(generateRegisterMethod(context))
    }

    private fun generateRegisterMethod(context: AdapterCodeBuilder.Context): MethodSpec {
        return MethodSpec.methodBuilder(REGISTER_METHOD_NAME).apply {
            addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            addParameter(AdapterFactoryRegister::class.java, REGISTER)
            addStatement("${REGISTER}.register(\$T.class, new $FACTORY_TYPE_NAME())",
                    context.metadata.type)
        }.build()
    }

    private fun generateFactoryType(context: AdapterCodeBuilder.Context): TypeSpec {
        val adapterClass = ClassName.get(AdapterFactory::class.java)
        val factoryType = ParameterizedTypeName.get(adapterClass, context.metadata.type, context.adapterClassName)
        return TypeSpec.classBuilder(FACTORY_TYPE_NAME).apply {
            addSuperinterface(factoryType)
            addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            addMethod(generateFactoryMethodAdapterId(context))
            addMethod(generateFactoryMethodCreate(context))
        }.build()
    }

    private fun generateFactoryMethodAdapterId(context: AdapterCodeBuilder.Context): MethodSpec {
        return MethodSpec.methodBuilder("adapterKey").apply {
            addAnnotation(Override::class.java)
            addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            addStatement("return ${context.idStaticFiledName}")
            returns(context.metadata.id.keyClass)
        }.build()
    }

    private fun generateFactoryMethodCreate(context: AdapterCodeBuilder.Context): MethodSpec {
        return MethodSpec.methodBuilder("create").apply {
            addAnnotation(Override::class.java)
            addAnnotation(Nonnull::class.java)
            addException(Exception::class.java)
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
            addCode("return new \$T($provider, $metadataStore);", context.adapterClassName)
            returns(context.adapterClassName)
        }.build()
    }
}