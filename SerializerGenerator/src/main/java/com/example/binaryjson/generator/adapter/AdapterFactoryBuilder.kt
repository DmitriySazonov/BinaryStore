package com.example.binaryjson.generator.adapter

import com.binarystore.adapter.AdapterFactory
import com.binarystore.adapter.AdapterFactoryRegister
import com.example.binaryjson.generator.TypeMetadata
import com.squareup.javapoet.*
import javax.annotation.Nonnull
import javax.lang.model.element.Modifier

object AdapterFactoryBuilder {

    const val REGISTER_METHOD_NAME = "registerInto"
    private const val FACTORY_TYPE_NAME = "Factory"
    private const val FACTORY_CONTEXT_NAME = "context"
    private const val REGISTER = "register"

    fun inject(
            builder: TypeSpec.Builder,
            adapterClassName: ClassName,
            metadata: TypeMetadata,
            idStaticFiledName: String,
    ) {
        builder.addType(generateFactoryType(metadata, adapterClassName, idStaticFiledName))
        builder.addMethod(generateRegisterMethod(metadata))
    }

    private fun generateRegisterMethod(metadata: TypeMetadata): MethodSpec {
        return MethodSpec.methodBuilder(REGISTER_METHOD_NAME).apply {
            addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            addParameter(AdapterFactoryRegister::class.java, REGISTER)
            addStatement("${REGISTER}.register(\$T.class, new $FACTORY_TYPE_NAME())", metadata.type)
        }.build()
    }

    private fun generateFactoryType(
            metadata: TypeMetadata,
            adapterClassName: ClassName,
            idStaticFiledName: String,
    ): TypeSpec {
        val adapterClass = ClassName.get(AdapterFactory::class.java)
        val factoryType = ParameterizedTypeName.get(adapterClass, metadata.type, adapterClassName)
        return TypeSpec.classBuilder(FACTORY_TYPE_NAME).apply {
            addSuperinterface(factoryType)
            addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            addMethod(generateFactoryMethodAdapterId(metadata, idStaticFiledName))
            addMethod(generateFactoryMethodCreate(adapterClassName))
        }.build()
    }

    private fun generateFactoryMethodAdapterId(
            metadata: TypeMetadata,
            idStaticFiledName: String,
    ): MethodSpec {
        return MethodSpec.methodBuilder("adapterKey").apply {
            addAnnotation(Override::class.java)
            addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            addStatement("return $idStaticFiledName")
            returns(metadata.id.keyClass)
        }.build()
    }

    private fun generateFactoryMethodCreate(adapterClassName: ClassName): MethodSpec {
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
            addCode("return new \$T($provider);", adapterClassName)
            returns(adapterClassName)
        }.build()
    }
}