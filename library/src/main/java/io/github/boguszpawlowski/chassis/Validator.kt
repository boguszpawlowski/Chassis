package io.github.boguszpawlowski.chassis

fun interface Validator<T> : (T) -> ValidationResult

sealed interface ValidationResult
object Unspecified : ValidationResult
object Valid : ValidationResult
interface Invalid : ValidationResult

fun notEmpty(invalid: Invalid = Empty): Validator<String> = Validator {
  if (it.isNotEmpty()) {
    Valid
  } else {
    invalid
  }
}

fun shorterThan(min: Int, invalid: Invalid = Empty): Validator<String> = Validator {
  if (it.length < min) {
    Valid
  } else {
    invalid
  }
}

fun notNull(invalid: Invalid = Empty): Validator<Any?> = Validator {
  if (it == null) {
    Valid
  } else {
    invalid
  }
}

object Empty : Invalid
