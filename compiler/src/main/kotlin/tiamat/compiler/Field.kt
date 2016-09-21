package tiamat.compiler

import com.squareup.javapoet.TypeName
import tiamat.Key

import javax.lang.model.element.Element
import javax.lang.model.element.VariableElement

class Field(element: Element, key: Key) {
    val prefKeyName: String = key.name
    val fieldType: TypeName = TypeName.get(element.asType())
    val name: String = element.simpleName.toString()
    val value: Any? = (element as VariableElement).constantValue
}
