package com.example.binaryjson.generator

import com.binarystore.IdType
import com.binarystore.Persistable
import com.example.binaryjson.generator.adapter.AdapterBuilder
import com.squareup.javapoet.TypeName
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes(value = ["com.binarystore.Persistable"])
class BinaryAdapterGenerator : AbstractProcessor() {

    override fun process(annotations: Set<TypeElement?>, roundEnv: RoundEnvironment): Boolean {
        if (annotations.isEmpty()) {
            return false
        }

        processPersistable(roundEnv)
        return true
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    private fun processPersistable(roundEnv: RoundEnvironment) {
        val clazz = Persistable::class.java
        roundEnv.getElementsAnnotatedWith(clazz).forEach { element ->
            if (element !is TypeElement) return@forEach
            BinaryAdapterVisitor().also {
                try {
                    val collector = FieldsCollector(element)
                    element.accept(it, collector)
                    val metadata = element.getMetadata(collector.fields)
                    val javaFile = AdapterBuilder().build(metadata)
                    FileHelper.write(processingEnv, javaFile)
                } catch (e: Throwable) {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, e.toString())
                }
            }
        }
    }

    private fun TypeElement.getMetadata(fields: List<FieldMeta>): TypeMetadata {
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
                fields = Collections.unmodifiableList(fields),
                element = this
        )
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
