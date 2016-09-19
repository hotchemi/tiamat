package tiamat.compiler

import tiamat.Pref
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.properties.Delegates

class PrefsProcessor : AbstractProcessor() {

    var filer: Filer by Delegates.notNull()
    var messager: Messager by Delegates.notNull()
    var ELEMENT_UTILS: Elements by Delegates.notNull()
    var TYPE_UTILS: Types by Delegates.notNull()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        messager = processingEnv.messager
        ELEMENT_UTILS = processingEnv.elementUtils
        TYPE_UTILS = processingEnv.typeUtils
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes(): Set<String> = hashSetOf(Pref::class.java.canonicalName)

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(Pref::class.java).forEach {
//            val processorUnit = findAndValidateProcessorUnit(processorUnits, it)
//            val rpe = RuntimePermissionsElement(it as TypeElement)
//            val javaFile = processorUnit.createJavaFile(rpe, requestCodeProvider)
//            javaFile.writeTo(filer)
        }
        return true
    }
}
