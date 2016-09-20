package tiamat.compiler

class TableNameDuplicateException(tableName: String) : RuntimeException("table name $tableName is already defined")

class TableNameNotDefinedException(className: String) : RuntimeException("$className should define table name")