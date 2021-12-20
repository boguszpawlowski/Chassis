package io.github.boguszpawlowski.chassis

public fun interface Validator<T> : (T) -> ValidationResult

public sealed interface ValidationResult
public object Unspecified : ValidationResult
public object Valid : ValidationResult
public interface Invalid : ValidationResult
public object Empty : Invalid

public fun notEmpty(invalid: Invalid = Empty): Validator<String> = Validator {
  if (it.isNotEmpty()) {
    Valid
  } else {
    invalid
  }
}

public fun shorterThan(min: Int, invalid: Invalid = Empty): Validator<String> = Validator {
  if (it.length < min) {
    Valid
  } else {
    invalid
  }
}

public fun notNull(invalid: Invalid = Empty): Validator<Any?> = Validator {
  if (it == null) {
    Valid
  } else {
    invalid
  }
}
