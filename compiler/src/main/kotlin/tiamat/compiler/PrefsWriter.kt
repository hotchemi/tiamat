package tiamat.compiler

import com.squareup.javapoet.*
import java.io.IOException
import java.util.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class PrefsWriter(val model: PrefsModel) {

    @Throws(IOException::class)
    fun write(filer: Filer) {
        val classBuilder = TypeSpec.classBuilder(model.className.simpleName())
        classBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        classBuilder.superclass(ClassName.get("tiamat", "RxSharedPreferences"))
        classBuilder.addFields(createFields())
        classBuilder.addMethods(createConstructors())
        classBuilder.addMethods(model.keys.flatMap { createMethods(it) })
        JavaFile.builder(model.className.packageName(), classBuilder.build()).build().writeTo(filer)
    }

    private fun createFields() =
            Arrays.asList(FieldSpec
                    .builder(String::class.java, "TABLE_NAME", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("\$S", model.tableName)
                    .build())

    private fun createConstructors() =
            Arrays.asList(
                    MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ClassName.get("android.content", "Context"), "context")
                            .addStatement("super(context, TABLE_NAME)")
                            .build(),
                    MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ClassName.get("android.content", "SharedPreferences"), "preferences")
                            .addStatement("super(preferences)")
                            .build())

    private fun createMethods(field: Field): List<MethodSpec> {
        val methodSpecs = ArrayList<MethodSpec>()
        when (field.fieldType) {
            TypeName.BOOLEAN -> {
                val argTypeOfSuperMethod = "boolean"
                val defaultValue = if (field.value == null) "false" else field.value.toString()
                val returnType = ClassName.get("java.lang", "Boolean")
                methodSpecs.add(createGetter(field, argTypeOfSuperMethod, returnType, defaultValue))
                methodSpecs.add(createSetter(field, argTypeOfSuperMethod))
                methodSpecs.add(createHasMethod(field))
                methodSpecs.add(createRemoveMethod(field))
            }
            ClassName.get(String::class.java) -> {
                val argTypeOfSuperMethod = "String"
                val defaultValue = if (field.value == null) "" else field.value.toString()
                val returnType = ClassName.get("java.lang", "String")
                methodSpecs.add(createSetter(field, argTypeOfSuperMethod))
                methodSpecs.add(createGetterForString(field, argTypeOfSuperMethod, returnType, defaultValue))
                methodSpecs.add(createHasMethod(field))
                methodSpecs.add(createRemoveMethod(field))
            }
            TypeName.FLOAT -> {
                val argTypeOfSuperMethod = "float"
                val defaultValue = if (field.value == null) "0.0F" else field.value.toString() + "f"
                val returnType = ClassName.get("java.lang", "Float")
                methodSpecs.add(createGetter(field, argTypeOfSuperMethod, returnType, defaultValue))
                methodSpecs.add(createSetter(field, argTypeOfSuperMethod))
                methodSpecs.add(createHasMethod(field))
                methodSpecs.add(createRemoveMethod(field))
            }
            TypeName.INT -> {
                val argTypeOfSuperMethod = "int"
                val defaultValue = if (field.value == null) "0" else field.value.toString()
                val returnType = ClassName.get("java.lang", "Integer")
                methodSpecs.add(createGetter(field, argTypeOfSuperMethod, returnType, defaultValue))
                methodSpecs.add(createSetter(field, argTypeOfSuperMethod))
                methodSpecs.add(createHasMethod(field))
                methodSpecs.add(createRemoveMethod(field))
            }
            TypeName.LONG -> {
                val argTypeOfSuperMethod = "long"
                val defaultValue = if (field.value == null) "0L" else field.value.toString() + "L"
                val returnType = ClassName.get("java.lang", "Long")
                methodSpecs.add(createGetter(field, argTypeOfSuperMethod, returnType, defaultValue))
                methodSpecs.add(createSetter(field, argTypeOfSuperMethod))
                methodSpecs.add(createHasMethod(field))
                methodSpecs.add(createRemoveMethod(field))
            }
            ParameterizedTypeName.get(Set::class.java, String::class.java) -> {
                val argTypeOfSuperMethod = "StringSet"
                val returnType = ParameterizedTypeName.get(Set::class.java, String::class.java)
                methodSpecs.add(createSetter(field, argTypeOfSuperMethod))
                methodSpecs.add(createGetterForSet(field, argTypeOfSuperMethod, returnType))
                methodSpecs.add(createHasMethod(field))
                methodSpecs.add(createRemoveMethod(field))
            }
            else -> {
                throw IllegalArgumentException("${field.fieldType} is not supported")
            }
        }
        return methodSpecs
    }

    private fun createGetter(field: Field, argTypeOfSuperMethod: String, returnType: ClassName, defaultValue: String) =
            MethodSpec.methodBuilder("get${field.name.capitalize()}")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ParameterizedTypeName.get(ClassName.get("tiamat", "Preference"), returnType))
                    .addStatement("return \$N(\$S, \$L)", "get${argTypeOfSuperMethod.capitalize()}", field.prefKeyName, defaultValue)
                    .build()

    private fun createGetterForString(field: Field, argTypeOfSuperMethod: String, returnType: TypeName, defaultValue: String) =
            MethodSpec.methodBuilder("get${field.name.capitalize()}")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ParameterizedTypeName.get(ClassName.get("tiamat", "Preference"), returnType))
                    .addStatement("return \$N(\$S, \$S)", "get${argTypeOfSuperMethod.capitalize()}", field.prefKeyName, defaultValue)
                    .build()

    private fun createGetterForSet(field: Field, argTypeOfSuperMethod: String, returnType: TypeName) =
            MethodSpec.methodBuilder("get${field.name.capitalize()}")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ParameterizedTypeName.get(ClassName.get("tiamat", "Preference"), returnType))
                    .addStatement("return \$N(\$S, new \$T<String>())", "get${argTypeOfSuperMethod.capitalize()}", field.prefKeyName, ClassName.get(HashSet::class.java))
                    .build()

    private fun createSetter(field: Field, argTypeOfSuperMethod: String) =
            MethodSpec.methodBuilder("set${field.name.capitalize()}")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(Void.TYPE)
                    .addParameter(field.fieldType, field.name)
                    .addStatement("\$N(\$S, \$N)", "put${argTypeOfSuperMethod.capitalize()}", field.prefKeyName, field.name)
                    .build()

    private fun createHasMethod(field: Field) =
            MethodSpec.methodBuilder("has${field.name.capitalize()}")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(Boolean::class.java)
                    .addStatement("return contains(\$S)", field.prefKeyName)
                    .build()

    private fun createRemoveMethod(field: Field) =
            MethodSpec.methodBuilder("remove${field.name.capitalize()}")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(Void.TYPE)
                    .addStatement("remove(\$S)", field.prefKeyName)
                    .build()
}
