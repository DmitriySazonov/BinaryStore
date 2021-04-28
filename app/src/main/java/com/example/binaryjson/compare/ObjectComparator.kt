package com.example.binaryjson.compare

object ObjectComparator {

    fun <T : Any> compare(reference: T, comparable: T): Boolean {
        return reference.javaClass.declaredFields.all {
            it.isAccessible = true
            val refValue = it.get(reference)
            val comValue = it.get(comparable)
            refValue == comValue || compare(refValue, comValue)
        }
    }
}