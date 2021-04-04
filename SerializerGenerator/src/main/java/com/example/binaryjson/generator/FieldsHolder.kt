package com.example.binaryjson.generator

class FieldsHolder(
        fields: List<FieldMetadata>
) {
    val fields = fields.sortedBy { it.name }
}