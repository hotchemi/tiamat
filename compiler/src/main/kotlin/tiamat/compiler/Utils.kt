package tiamat.compiler

import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

fun getPackageName(elementUtils: Elements, type: TypeElement): String {
    return elementUtils.getPackageOf(type).qualifiedName.toString()
}

fun getClassName(type: TypeElement, packageName: String): String {
    val packageLen = packageName.length + 1
    return type.qualifiedName.toString().substring(packageLen).replace('.', '$')
}
