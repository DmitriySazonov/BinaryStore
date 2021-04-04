package com.example.binaryjson.generator

import com.binarystore.Version
import com.squareup.javapoet.ClassName
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

class FieldsCollector(
        private val element: TypeElement
) {
    val fields = ArrayList<FieldMetadata>()

    fun addField(variable: VariableElement) {
        if (!variable.kind.isField) return
        if (variable.enclosingElement != element) return
        val varName = variable.simpleName.toString()
        variable.enclosingElement
        val className = ClassName.get(variable.asType())
        val version = variable.getVersion()
        fields += FieldMetadata(
                name = varName,
                type = className,
                version = version
        )
    }

    private fun VariableElement.getVersion(): FieldMetadata.Version? {
        return getAnnotation(Version::class.java)?.toVersionMeta()
    }

    private fun Version.toVersionMeta(): FieldMetadata.Version {
        return FieldMetadata.Version(
                id = id,
                fallback = fallback
        )
    }
}