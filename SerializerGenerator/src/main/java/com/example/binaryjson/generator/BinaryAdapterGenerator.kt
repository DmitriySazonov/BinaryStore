package com.example.binaryjson.generator

import com.binarystore.IdType
import com.binarystore.Persistable
import com.example.binaryjson.generator.adapter.AdapterBuilder
import com.example.binaryjson.generator.registrator.RegistratorBuilder
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes(value = ["com.binarystore.Persistable"])
class BinaryAdapterGenerator : AbstractProcessor() {

    override fun process(annotations: Set<TypeElement?>, roundEnv: RoundEnvironment): Boolean {
        if (annotations.isEmpty()) {
            return false
        }

        processPersistable(roundEnv)
                .also(::generateRegistrator)

        return true
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    private fun generateRegistrator(adapters: List<ClassName>) {
        try {
            FileHelper.write(processingEnv, RegistratorBuilder.build(adapters))
        } catch (e: Throwable) {
            e.printStackTrace()
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, e.toString())
        }
    }

    private fun processPersistable(roundEnv: RoundEnvironment): List<ClassName> {
        val clazz = Persistable::class.java
        val adapterBuilder = AdapterBuilder(processingEnv)
        return roundEnv.getElementsAnnotatedWith(clazz).mapNotNull { element ->
            if (element !is TypeElement) return@mapNotNull null
            try {
                val metadata = element.getMetadata()
                val javaFile = adapterBuilder.build(metadata)
                FileHelper.write(processingEnv, javaFile)
                ClassName.get(javaFile.packageName, javaFile.typeSpec.name)
            } catch (e: Throwable) {
                e.printStackTrace()
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, e.toString())
                null
            }
        }
    }

    private fun TypeElement.getMetadata(): TypeMetadata {
        val annotation = getAnnotation(Persistable::class.java)
        val id = when (annotation.idType) {
            IdType.STRING -> Id.String(annotation.id)
            IdType.INT -> Id.Int(annotation.id.toIntOrNull()
                    ?: throwIdBadType(this, annotation))
        }
        return TypeMetadata(
                id = id,
                versionId = annotation.versionId,
                injectType = annotation.inject,
                fields = Collections.unmodifiableList(findAllFields()),
                element = this,
                constructors = findAllConstructors()
        )
    }

    private fun TypeElement.findAllFields(): List<Field> {
        val collector = FieldsCollector(this, processingEnv)
        processingEnv.elementUtils.getAllMembers(this).forEach {
            collector.addField(it as? VariableElement ?: return@forEach)
        }
        return collector.fields
    }

    private fun TypeElement.findAllConstructors(): List<Constructor> {
        val constructorCollector = ConstructorCollector()
        enclosedElements.forEach {
            if (it !is ExecutableElement) return@forEach
            if (it.kind != ElementKind.CONSTRUCTOR) return@forEach
            constructorCollector.addConstructor(it)
        }

        return constructorCollector.constructors
    }

    private fun throwIdBadType(element: TypeElement, persistable: Persistable): Nothing {
        TypeName.get(element.asType())
        throw IllegalArgumentException(
                "Bad id for type ${TypeName.get(element.asType())} " +
                        "In annotation declared type(${persistable.idType}) for id " +
                        "But was provided '${persistable.id}'"
        )
    }
}
