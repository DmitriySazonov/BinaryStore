package com.example.binaryjson.generator.registrator

import com.binarystore.adapter.AdapterFactoryRegister
import com.example.binaryjson.generator.adapter.AdapterFactoryBuilder
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

object RegistratorBuilder {

    private const val PACKAGE = "com.binarystore"
    private const val NAME = "AdaptersRegistrator"

    private const val REGISTER = "register"

    fun build(adapters: List<ClassName>): JavaFile {
        val typeSpec: TypeSpec = TypeSpec.classBuilder(NAME).apply {
            addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            addMethod(generateRegisterMethod(adapters))
        }.build()
        return JavaFile.builder(PACKAGE, typeSpec).build()
    }

    private fun generateRegisterMethod(adapters: List<ClassName>): MethodSpec {
        return MethodSpec.methodBuilder("registerInto").apply {
            addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            addParameter(AdapterFactoryRegister::class.java, REGISTER)
            val method = AdapterFactoryBuilder.REGISTER_METHOD_NAME
            adapters.forEach {
                addStatement("\$T.$method($REGISTER)", it)
            }
        }.build()
    }
}