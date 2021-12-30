package io.github.boguszpawlowski.chassis

public fun interface Validator<T> : (T) -> ValidationResult

public sealed interface ValidationResult
public object Unspecified : ValidationResult
public object Valid : ValidationResult
public interface Invalid : ValidationResult
public object DefaultInvalid : Invalid

@Suppress("NOTHING_TO_INLINE")
public inline infix fun <T> Validator<T>.or(other: Validator<T>): Validator<T> = Validator {
  if (this(it) == Valid) {
    Valid
  } else {
    other(it)
  }
}

@Suppress("NOTHING_TO_INLINE")
public inline infix fun <T> Validator<T>.and(other: Validator<T>): Validator<T> = Validator {
  val result = this(it)
  if (result == Valid) {
    other(it)
  } else {
    result
  }
}

public fun notEmpty(invalid: Invalid = DefaultInvalid): Validator<String?> = Validator {
  if (it.isNullOrEmpty().not()) {
    Valid
  } else {
    invalid
  }
}

public fun shorterThan(max: Int, invalid: Invalid = DefaultInvalid): Validator<String?> =
  Validator {
    if (it.orEmpty().length < max) {
      Valid
    } else {
      invalid
    }
  }

public fun longerThan(min: Int, invalid: Invalid = DefaultInvalid): Validator<String?> = Validator {
  if (it.orEmpty().length > min) {
    Valid
  } else {
    invalid
  }
}

public fun exactly(length: Int, invalid: Invalid = DefaultInvalid): Validator<String?> = Validator {
  if (it.orEmpty().length == length) {
    Valid
  } else {
    invalid
  }
}

public fun <T> isNull(invalid: Invalid = DefaultInvalid): Validator<T?> = Validator {
  if (it == null) {
    Valid
  } else {
    invalid
  }
}

public fun matches(regex: Regex, invalid: Invalid = DefaultInvalid): Validator<String?> =
  Validator {
    if (regex.matches(it.orEmpty())) {
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
