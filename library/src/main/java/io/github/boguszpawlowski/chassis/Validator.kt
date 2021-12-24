package io.github.boguszpawlowski.chassis

public fun interface Validator<T> : (T) -> ValidationResult

public sealed interface ValidationResult
public object Unspecified : ValidationResult
public object Valid : ValidationResult
public interface Invalid : ValidationResult
public object DefaultInvalid : Invalid

public fun notEmpty(invalid: Invalid = DefaultInvalid): Validator<String> = Validator {
  if (it.isNotEmpty()) {
    Valid
  } else {
    invalid
  }
}

public fun shorterThan(max: Int, invalid: Invalid = DefaultInvalid): Validator<String> = Validator {
  if (it.length < max) {
    Valid
  } else {
    invalid
  }
}

public fun longerThan(min: Int, invalid: Invalid = DefaultInvalid): Validator<String> = Validator {
  if (it.length > min) {
    Valid
  } else {
    invalid
  }
}

public fun matches(regex: Regex, invalid: Invalid = DefaultInvalid): Validator<String> = Validator {
  if (regex.matches(it)) {
    Valid
  } else {
    invalid
  }
}

public fun notNull(invalid: Invalid = DefaultInvalid): Validator<Any?> = Validator {
  if (it != null) {
    Valid
  } else {
    invalid
  }
}

public fun required(invalid: Invalid = DefaultInvalid): Validator<Boolean?> = Validator {
  if (it == true) {
    Valid
  } else {
    invalid
  }
}
