package tiamat.compiler

import com.google.common.base.CaseFormat
import tiamat.Pref
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

fun parseEnv(env: RoundEnvironment, elementUtils: Elements): List<PrefsModel> {
    val models = env.getElementsAnnotatedWith(Pref::class.java).map {
        PrefsModel(it as TypeElement, elementUtils)
    }
    validateSchemaModel(models)
    return models
}

fun validateSchemaModel(models: List<PrefsModel>) {
    val tableNames = HashSet<String>()
    models.forEach {
        val tableName = it.tableName
        if (tableName.isEmpty()) {
            throw TableNameNotDefinedException(it.originalClassName)
        }
        if (tableNames.contains(tableName)) {
            throw TableNameDuplicateException(tableName)
        }
        tableNames.add(tableName)
    }
}

fun getPackageName(elementUtils: Elements, type: TypeElement) =
        elementUtils.getPackageOf(type).qualifiedName.toString()


fun getClassName(type: TypeElement, packageName: String) =
        type.qualifiedName.toString().substring(packageName.length + 1).replace('.', '$')

fun upperCamelToLowerSnake(name: String): String = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)

fun lowerCamelToLowerSnake(name: String): String = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)

// Exceptions

class TableNameDuplicateException(tableName: String) : RuntimeException("table name $tableName is already defined")

class TableNameNotDefinedException(className: String) : RuntimeException("$className should define table name")
