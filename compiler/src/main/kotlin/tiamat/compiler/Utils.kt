package tiamat.compiler

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

fun getPackageName(elementUtils: Elements, type: TypeElement): String {
    return elementUtils.getPackageOf(type).qualifiedName.toString()
}

fun getClassName(type: TypeElement, packageName: String): String {
    val packageLen = packageName.length + 1
    return type.qualifiedName.toString().substring(packageLen).replace('.', '$')
}
