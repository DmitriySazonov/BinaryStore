package com.example.binaryjson.generator

import javax.lang.model.element.VariableElement
import javax.lang.model.util.ElementScanner7

internal class BinaryAdapterVisitor : ElementScanner7<Void?, FieldsCollector>() {

    override fun visitVariable(variable: VariableElement, collector: FieldsCollector): Void? {
        collector.addField(variable)
        return super.visitVariable(variable, collector)
    }
}
