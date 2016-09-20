package tiamat.compiler

import com.squareup.javapoet.ClassName
import tiamat.Key
import tiamat.Pref
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

class PrefsModel(element: TypeElement, elementUtils: Elements) {

    val originalClassName: String
    val className: ClassName
    val tableName: String
    val keys = ArrayList<Field>()

    init {
        val pref = element.getAnnotation(Pref::class.java)
        this.tableName = pref.name
        val packageName = getPackageName(elementUtils, element)
        this.originalClassName = getClassName(element, packageName)
        this.className = ClassName.get(packageName, originalClassName.replace("Schema", ""))
        findAnnotations(element)
    }

    private fun findAnnotations(element: Element) {
        element.enclosedElements.forEach {
            findAnnotations(it)
            val key = it.getAnnotation(Key::class.java)
            if (key != null) {
                keys.add(Field(it, key))
            }
        }
    }
}
