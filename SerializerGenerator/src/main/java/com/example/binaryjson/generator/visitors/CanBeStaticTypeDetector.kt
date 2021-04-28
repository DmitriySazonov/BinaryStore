package com.example.binaryjson.generator.visitors

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.ArrayType
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.util.TypeKindVisitor7

class CanBeStaticTypeDetector(
        private val env: ProcessingEnvironment,
) : TypeKindVisitor7<Boolean?, Void>() {
    override fun visitDeclared(declaredType: DeclaredType, p1: Void?): Boolean {
        val type = (declaredType.asElement() as? TypeElement) ?: return false
        return type.kind == ElementKind.CLASS && Modifier.ABSTRACT !in type.modifiers
    }

    override fun visitPrimitive(p0: PrimitiveType?, p1: Void?): Boolean {
        return true
    }

    override fun visitArray(array: ArrayType, p1: Void?): Boolean? {
        return array.componentType.accept(this, p1)
    }
}