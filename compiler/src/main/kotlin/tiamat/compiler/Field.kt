package tiamat.compiler

import com.squareup.javapoet.TypeName
import tiamat.Key
import javax.lang.model.element.Element
import javax.lang.model.element.VariableElement

class Field(element: Element, key: Key) {
    val name: String = element.simpleName.toString()
    val prefKeyName: String = if (key.name.isNotBlank()) key.name else lowerCamelToLowerSnake(name)
    val fieldType: TypeName = TypeName.get(element.asType())
    val value: Any? = (element as VariableElement).constantValue
}
