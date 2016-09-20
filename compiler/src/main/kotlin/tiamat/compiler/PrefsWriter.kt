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
        classBuilder.addMethods(createMethods())
        JavaFile.builder(model.className.packageName(), classBuilder.build()).build().writeTo(filer)
    }

    private fun createFields(): List<FieldSpec> {
        val fieldSpecs = ArrayList<FieldSpec>(1)
        fieldSpecs.add(FieldSpec.builder(String::class.java, "TABLE_NAME", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("\$S", model.tableName).build())
        return fieldSpecs
    }

    private fun createConstructors(): List<MethodSpec> {
        val methodSpecs = ArrayList<MethodSpec>(2)
        methodSpecs.add(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .addStatement("super(context, TABLE_NAME)")
                .build())
        methodSpecs.add(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.content", "SharedPreferences"), "preferences")
                .addStatement("super(preferences)")
                .build())
        return methodSpecs
    }

    private fun createMethods(): List<MethodSpec> {
        val methodSpecs = ArrayList<MethodSpec>(model.keys.size)
        model.keys.forEach {
            methodSpecs.addAll(createMethods(it))
        }
        return methodSpecs
    }

    private fun createMethods(field: Field): List<MethodSpec> {
        val methodSpecs = ArrayList<MethodSpec>()

        when (field.fieldType) {
            TypeName.BOOLEAN -> {
                val argTypeOfSuperMethod = "boolean"
                val defaultValue = if (field.value == null) "false" else field.value.toString()
                methodSpecs.add(createGetterWithDefaultValue(field, argTypeOfSuperMethod))
                methodSpecs.add(createGetter(field, argTypeOfSuperMethod, defaultValue))
                methodSpecs.addAll(createSetter(field, argTypeOfSuperMethod))
                methodSpecs.add(createHasMethod(field))
                methodSpecs.add(createRemoveMethod(field))
            }
            ClassName.get(String::class.java) -> {
                val argTypeOfSuperMethod = "String"
                val defaultValue = if (field.value == null) "" else field.value.toString()
                val methodName = "get${field.name.capitalize()}"
                val superMethodName = "get${argTypeOfSuperMethod.capitalize()}"
                methodSpecs.add(MethodSpec.methodBuilder(methodName)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(field.fieldType)
                        .addStatement("return \$N(\$S, \$S)", superMethodName, field.prefKeyName, defaultValue)
                        .build())
                methodSpecs.addAll(createSetter(field, argTypeOfSuperMethod))
                methodSpecs.add(createGetterWithDefaultValue(field, argTypeOfSuperMethod))
                methodSpecs.add(createHasMethod(field))
                methodSpecs.add(createRemoveMethod(field))
            }
            TypeName.FLOAT -> {
                val argTypeOfSuperMethod = "float"
                val defaultValue = if (field.value == null) "0.0F" else field.value.toString() + "f"
                methodSpecs.add(createGetterWithDefaultValue(field, argTypeOfSuperMethod))
                methodSpecs.add(createGetter(field, argTypeOfSuperMethod, defaultValue))
                methodSpecs.addAll(createSetter(field, argTypeOfSuperMethod))
                methodSpecs.add(createHasMethod(field))
                methodSpecs.add(createRemoveMethod(field))
            }
            TypeName.INT -> {
                val argTypeOfSuperMethod = "int"
                val defaultValue = if (field.value == null) "0" else field.value.toString()
                methodSpecs.add(createGetterWithDefaultValue(field, argTypeOfSuperMethod))
                methodSpecs.add(createGetter(field, argTypeOfSuperMethod, defaultValue))
                methodSpecs.addAll(createSetter(field, argTypeOfSuperMethod))
                methodSpecs.add(createHasMethod(field))
                methodSpecs.add(createRemoveMethod(field))
            }
            TypeName.LONG -> {
                val argTypeOfSuperMethod = "long"
                val defaultValue = if (field.value == null) "0L" else field.value.toString() + "L"
                methodSpecs.add(createGetterWithDefaultValue(field, argTypeOfSuperMethod))
                methodSpecs.add(createGetter(field, argTypeOfSuperMethod, defaultValue))
                methodSpecs.addAll(createSetter(field, argTypeOfSuperMethod))
                methodSpecs.add(createHasMethod(field))
                methodSpecs.add(createRemoveMethod(field))
            }
            ParameterizedTypeName.get(Set::class.java, String::class.java) -> {
                val argTypeOfSuperMethod = "StringSet"
                val methodName = "get${field.name.capitalize()}"
                val superMethodName = "get${argTypeOfSuperMethod.capitalize()}"
                methodSpecs.add(MethodSpec.methodBuilder(methodName)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(field.fieldType)
                        .addStatement("return \$N(\$S, new \$T<String>())", superMethodName, field.prefKeyName, ClassName.get(HashSet::class.java))
                        .build())
                methodSpecs.add(createGetterWithDefaultValue(field, argTypeOfSuperMethod))
                methodSpecs.addAll(createSetter(field, argTypeOfSuperMethod))
                methodSpecs.add(createHasMethod(field))
                methodSpecs.add(createRemoveMethod(field))
            }
            else -> {
                throw IllegalArgumentException("${field.fieldType} is not supported")
            }
        }
        return methodSpecs
    }

    private fun createGetterWithDefaultValue(field: Field, argTypeOfSuperMethod: String): MethodSpec {
        val methodName = "get${field.name.capitalize()}"
        val superMethodName = "get${argTypeOfSuperMethod.capitalize()}"
        val parameterName = "defValue"
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(field.fieldType, parameterName)
                .returns(field.fieldType)
                .addStatement("return \$N(\$S, \$N)", superMethodName, field.prefKeyName, parameterName)
                .build()
    }

    private fun createGetter(field: Field, argTypeOfSuperMethod: String, defaultValue: String): MethodSpec {
        return MethodSpec.methodBuilder("get${field.name.capitalize()}")
                .addModifiers(Modifier.PUBLIC)
                .returns(field.fieldType)
                .addStatement("return \$N(\$S, \$L)", "get${argTypeOfSuperMethod.capitalize()}", field.prefKeyName, defaultValue)
                .build()
    }

    private fun createSetter(field: Field, argTypeOfSuperMethod: String): Collection<MethodSpec> {
        val methodSpecs = ArrayList<MethodSpec>()
        run {
            val methodName = "set${field.name.capitalize()}"
            val superMethodName = "put${argTypeOfSuperMethod.capitalize()}"
            methodSpecs.add(MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(Void.TYPE)
                    .addParameter(field.fieldType, field.name)
                    .addStatement("\$N(\$S, \$N)", superMethodName, field.prefKeyName, field.name)
                    .build())
        }
        run {
            val methodName = "put${field.name.capitalize()}"
            val superMethodName = "put${argTypeOfSuperMethod.capitalize()}"
            methodSpecs.add(MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(Void.TYPE)
                    .addParameter(field.fieldType, field.name)
                    .addStatement("\$N(\$S, \$N)", superMethodName, field.prefKeyName, field.name)
                    .build())
        }
        return methodSpecs
    }

    private fun createHasMethod(field: Field): MethodSpec {
        return MethodSpec.methodBuilder("has${field.name.capitalize()}")
                .addModifiers(Modifier.PUBLIC)
                .returns(Boolean::class.java)
                .addStatement("return has(\$S)", field.prefKeyName)
                .build()
    }

    private fun createRemoveMethod(field: Field): MethodSpec {
        return MethodSpec.methodBuilder("remove${field.name.capitalize()}")
                .addModifiers(Modifier.PUBLIC)
                .returns(Void::class.java)
                .addStatement("remove(\$S)", field.prefKeyName)
                .build()
    }
}
