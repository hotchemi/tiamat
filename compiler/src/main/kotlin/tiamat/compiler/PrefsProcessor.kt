package tiamat.compiler

import tiamat.Pref
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import kotlin.properties.Delegates

class PrefsProcessor : AbstractProcessor() {

    var filer: Filer by Delegates.notNull()
    var messager: Messager by Delegates.notNull()
    var elementUtils: Elements by Delegates.notNull()
    var typeUtils: Types by Delegates.notNull()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        messager = processingEnv.messager
        elementUtils = processingEnv.elementUtils
        typeUtils = processingEnv.typeUtils
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes(): Set<String> = hashSetOf(Pref::class.java.canonicalName)

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        parseEnv(roundEnv, elementUtils)
                .map { PrefsWriter(it) }
                .forEach {
                    try {
                        it.write(filer)
                    } catch (e: IOException) {
                        messager.printMessage(Diagnostic.Kind.ERROR, e.message)
                    }
                }
        return true
    }
}
