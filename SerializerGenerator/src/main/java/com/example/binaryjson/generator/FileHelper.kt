package com.example.binaryjson.generator

import com.squareup.javapoet.JavaFile
import java.io.BufferedWriter
import java.io.IOException
import javax.annotation.processing.ProcessingEnvironment
import javax.tools.Diagnostic
import javax.tools.JavaFileObject

object FileHelper {

    fun write(
            env: ProcessingEnvironment,
            javaFile: JavaFile
    ) {
        try {
            val sourceFile: JavaFileObject = env.filer.createSourceFile(
                    javaFile.packageName + "." + javaFile.typeSpec.name)
            BufferedWriter(sourceFile.openWriter()).use { writer ->
                javaFile.writeTo(writer)
            }
        } catch (e: IOException) {
            env.messager.printMessage(Diagnostic.Kind.ERROR, e.toString())
        }
    }
}