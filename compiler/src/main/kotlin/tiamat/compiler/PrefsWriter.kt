package com.rejasupotaro.android.kvs.internal

import com.squareup.javapoet.*
import tiamat.compiler.PrefsModel
import java.io.IOException
import java.util.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class PrefsWriter(val model: PrefsModel) {

    @Throws(IOException::class)
    fun write(filer: Filer) {
        val classBuilder = TypeSpec.classBuilder(model.className.simpleName())
        classBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        val superClassName = ClassName.get(PrefsSchema::class.java!!)
        classBuilder.superclass(superClassName)

        val fieldSpecs = createFields()
        classBuilder.addFields(fieldSpecs)

        val methodSpecs = ArrayList<MethodSpec>()
        methodSpecs.addAll(createConstructors())
        methodSpecs.add(createInitializeMethod())
        methodSpecs.addAll(createMethods())
        classBuilder.addMethods(methodSpecs)

        val outClass = classBuilder.build()
        JavaFile.builder(model.className.packageName(), outClass).build().writeTo(filer)
    }

    private fun createFields(): List<FieldSpec> {
        val fieldSpecs = ArrayList<FieldSpec>()
        fieldSpecs.add(FieldSpec.builder(String::class.java, "TABLE_NAME", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("\$S", model.getTableName()).build())
        fieldSpecs.add(FieldSpec.builder(model.getClassName(), "singleton", Modifier.PRIVATE, Modifier.STATIC).build())
        return fieldSpecs
    }

    private fun createConstructors(): List<MethodSpec> {
        val methodSpecs = ArrayList<MethodSpec>()
        methodSpecs.add(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).addParameter(ClassName.get("android.content", "Context"), "context").addStatement("init(context, TABLE_NAME)").build())
        methodSpecs.add(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).addParameter(ClassName.get("android.content", "SharedPreferences"), "prefs").addStatement("init(prefs)").build())
        return methodSpecs
    }

    private fun createInitializeMethod(): MethodSpec {
        if ("java.lang.Object" == model.getBuilderClassFqcn()) {
            return MethodSpec.methodBuilder("get").addModifiers(Modifier.PUBLIC, Modifier.STATIC).returns(model.getClassName()).addParameter(ClassName.get("android.content", "Context"), "context").addStatement("if (singleton != null) return singleton").addStatement("synchronized (\$N.class) { if (singleton == null) singleton = new \$N(context); }",
                    model.getClassName().simpleName(),
                    model.getClassName().simpleName()).addStatement("return singleton").build()
        } else {
            return MethodSpec.methodBuilder("get").addModifiers(Modifier.PUBLIC, Modifier.STATIC).returns(model.getClassName()).addParameter(ClassName.get("android.content", "Context"), "context").addStatement("if (singleton != null) return singleton").addStatement("synchronized (\$N.class) { if (singleton == null) singleton = new \$N().build(context); }",
                    model.getClassName().simpleName(),
                    model.getBuilderClassFqcn()).addStatement("return singleton").build()
        }
    }

    private fun createMethods(): List<MethodSpec> {
        val methodSpecs = ArrayList<MethodSpec>()
        for (field in model.getKeys()) {
            methodSpecs.addAll(createMethods(field))
        }
        return methodSpecs
    }

    private fun createMethods(field: Field): List<MethodSpec> {
        val methodSpecs = ArrayList<MethodSpec>()
        if (TypeName.BOOLEAN == field.getFieldType()) {
            val argTypeOfSuperMethod = "boolean"
            val defaultValue = if (field.getValue() == null)
                "false"
            else
                field.getValue().toString()
            methodSpecs.add(createGetterWithDefaultValue(field, argTypeOfSuperMethod))
            methodSpecs.add(createGetter(field, argTypeOfSuperMethod, defaultValue))
            methodSpecs.addAll(createSetter(field, argTypeOfSuperMethod))
            methodSpecs.add(createHasMethod(field))
            methodSpecs.add(createRemoveMethod(field))
        } else if (ClassName.get(String::class.java) == field.getFieldType()) {
            val argTypeOfSuperMethod = "String"
            val defaultValue = if (field.getValue() == null)
                ""
            else
                field.getValue().toString()
            val methodName = "get" + StringUtils.capitalize(field.getName())
            val superMethodName = "get" + StringUtils.capitalize(argTypeOfSuperMethod)
            if (field.hasSerializer()) {
                methodSpecs.add(MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).returns(field.getSerializeType()).addStatement("return new \$T().deserialize(\$N(\$S, \"\"))",
                        field.getSerializerType(),
                        superMethodName,
                        field.getPrefKeyName()).build())
            } else {
                methodSpecs.add(MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).returns(field.getFieldType()).addStatement("return \$N(\$S, \$S)", superMethodName, field.getPrefKeyName(), defaultValue).build())
            }
            methodSpecs.addAll(createSetter(field, argTypeOfSuperMethod))
            methodSpecs.add(createGetterWithDefaultValue(field, argTypeOfSuperMethod))
            methodSpecs.add(createHasMethod(field))
            methodSpecs.add(createRemoveMethod(field))
        } else if (TypeName.FLOAT == field.getFieldType()) {
            val argTypeOfSuperMethod = "float"
            val defaultValue = if (field.getValue() == null)
                "0.0F"
            else
                field.getValue().toString() + "f"
            methodSpecs.add(createGetterWithDefaultValue(field, argTypeOfSuperMethod))
            methodSpecs.add(createGetter(field, argTypeOfSuperMethod, defaultValue))
            methodSpecs.addAll(createSetter(field, argTypeOfSuperMethod))
            methodSpecs.add(createHasMethod(field))
            methodSpecs.add(createRemoveMethod(field))
        } else if (TypeName.INT == field.getFieldType()) {
            val argTypeOfSuperMethod = "int"
            val defaultValue = if (field.getValue() == null)
                "0"
            else
                field.getValue().toString()
            methodSpecs.add(createGetterWithDefaultValue(field, argTypeOfSuperMethod))
            methodSpecs.add(createGetter(field, argTypeOfSuperMethod, defaultValue))
            methodSpecs.addAll(createSetter(field, argTypeOfSuperMethod))
            methodSpecs.add(createHasMethod(field))
            methodSpecs.add(createRemoveMethod(field))
        } else if (TypeName.LONG == field.getFieldType()) {
            val argTypeOfSuperMethod = "long"
            val defaultValue = if (field.getValue() == null)
                "0L"
            else
                field.getValue().toString() + "L"
            methodSpecs.add(createGetterWithDefaultValue(field, argTypeOfSuperMethod))
            methodSpecs.add(createGetter(field, argTypeOfSuperMethod, defaultValue))
            methodSpecs.addAll(createSetter(field, argTypeOfSuperMethod))
            methodSpecs.add(createHasMethod(field))
            methodSpecs.add(createRemoveMethod(field))
        } else if (ParameterizedTypeName.get(Set<*>::class.java!!, String::class.java) == field.getFieldType()) {
            val argTypeOfSuperMethod = "StringSet"
            val methodName = "get" + StringUtils.capitalize(field.getName())
            val superMethodName = "get" + StringUtils.capitalize(argTypeOfSuperMethod)

            methodSpecs.add(MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).returns(field.getFieldType()).addStatement("return \$N(\$S, new \$T<String>())", superMethodName, field.getPrefKeyName(), ClassName.get(HashSet<*>::class.java!!)).build())
            methodSpecs.add(createGetterWithDefaultValue(field, argTypeOfSuperMethod))
            methodSpecs.addAll(createSetter(field, argTypeOfSuperMethod))
            methodSpecs.add(createHasMethod(field))
            methodSpecs.add(createRemoveMethod(field))
        } else {
            throw IllegalArgumentException(field.getFieldType() + " is not supported")
        }

        return methodSpecs
    }

    private fun createGetterWithDefaultValue(field: Field, argTypeOfSuperMethod: String): MethodSpec {
        val methodName = "get" + StringUtils.capitalize(field.getName())
        val superMethodName = "get" + StringUtils.capitalize(argTypeOfSuperMethod)
        val parameterName = "defValue"
        if (field.hasSerializer()) {
            return MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).addParameter(field.getFieldType(), parameterName).returns(field.getSerializeType()).addStatement("return new \$T().deserialize(\$N(\$S, \$L))",
                    field.getSerializerType(),
                    superMethodName,
                    field.getPrefKeyName(),
                    parameterName).build()
        } else {
            return MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).addParameter(field.getFieldType(), parameterName).returns(field.getFieldType()).addStatement("return \$N(\$S, \$N)", superMethodName, field.getPrefKeyName(), parameterName).build()
        }
    }

    private fun createGetter(field: Field, argTypeOfSuperMethod: String, defaultValue: String): MethodSpec {
        val methodName = "get" + StringUtils.capitalize(field.getName())
        val superMethodName = "get" + StringUtils.capitalize(argTypeOfSuperMethod)
        if (field.hasSerializer()) {
            return MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).returns(field.getSerializeType()).addStatement("return new \$T().deserialize(\$N(\$S, \$L))",
                    field.getSerializerType(),
                    superMethodName,
                    field.getPrefKeyName(),
                    defaultValue).build()
        } else {
            return MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).returns(field.getFieldType()).addStatement("return \$N(\$S, \$L)", superMethodName, field.getPrefKeyName(), defaultValue).build()
        }
    }

    private fun createSetter(field: Field, argTypeOfSuperMethod: String): Collection<MethodSpec> {
        val methodSpecs = ArrayList<MethodSpec>()
        run {
            val methodName = "set" + StringUtils.capitalize(field.getName())
            val superMethodName = "put" + StringUtils.capitalize(argTypeOfSuperMethod)
            if (field.hasSerializer()) {
                methodSpecs.add(MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).returns(Void.TYPE).addParameter(field.getSerializeType(), field.getName()).addStatement("\$N(\$S, new \$T().serialize(\$N))",
                        superMethodName,
                        field.getPrefKeyName(),
                        field.getSerializerType(),
                        field.getName()).build())
            } else {
                methodSpecs.add(MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).returns(Void.TYPE).addParameter(field.getFieldType(), field.getName()).addStatement("\$N(\$S, \$N)", superMethodName, field.getPrefKeyName(), field.getName()).build())
            }
        }

        run {
            val methodName = "put" + StringUtils.capitalize(field.getName())
            val superMethodName = "put" + StringUtils.capitalize(argTypeOfSuperMethod)
            if (field.hasSerializer()) {
                methodSpecs.add(MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).returns(Void.TYPE).addParameter(field.getSerializeType(), field.getName()).addStatement("\$N(\$S, new \$T().serialize(\$N))",
                        superMethodName,
                        field.getPrefKeyName(),
                        field.getSerializerType(),
                        field.getName()).build())
            } else {
                methodSpecs.add(MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).returns(Void.TYPE).addParameter(field.getFieldType(), field.getName()).addStatement("\$N(\$S, \$N)", superMethodName, field.getPrefKeyName(), field.getName()).build())
            }
        }

        return methodSpecs
    }

    private fun createHasMethod(field: Field): MethodSpec {
        val methodName = "has" + StringUtils.capitalize(field.getName())
        return MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).returns(Boolean.TYPE).addStatement("return has(\$S)", field.getPrefKeyName()).build()
    }

    private fun createRemoveMethod(field: Field): MethodSpec {
        val methodName = "remove" + StringUtils.capitalize(field.getName())
        return MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC).returns(Void.TYPE).addStatement("remove(\$S)", field.getPrefKeyName()).build()
    }
}
