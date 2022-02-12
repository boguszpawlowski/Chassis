package io.github.boguszpawlowski.chassis

/**
 * A functional interface of a smallest component.
 */
public fun interface Validator<T> : (T) -> ValidationResult

/**
 * A root interface for the validation results class hierarchy. You should never directly implement it. Use `Invalid` for defining your own validation failure domain.
 */
public sealed interface ValidationResult

/**
 * A default validation state of the required fields, before any user input.
 */
internal object Unspecified : ValidationResult

/**
 * A class representing valid state of the field.
 */
public object Valid : ValidationResult

/**
 * A root interface for the validation failure class hierarchy. Implement it in order to return your custom failures from the validators.
 */
public interface Invalid : ValidationResult

/**
 * Default invalid cause, will be returned by a built-in library validators. It doesn't provide any context or data about the failure.
 */
public object DefaultInvalid : Invalid

/**
 * A function that allows to compose independent validators.
 *
 * login = field {
 *   validators(shorterThan(2) or longerThan(10))
 *   ...
 * }
 * Above field will be valid with input shorter than 2 or longer than 10 chars.
 */
@Suppress("NOTHING_TO_INLINE")
public inline infix fun <T> Validator<T>.or(other: Validator<T>): Validator<T> = Validator {
  if (this(it) == Valid) {
    Valid
  } else {
    other(it)
  }
}

/**
 * A validator, which will return Valid for every non-empty input
 * @param invalid a `Invalid` interface instance which will be returned on failure.
 */
public fun notEmpty(invalid: Invalid = DefaultInvalid): Validator<String?> = Validator {
  if (it.isNullOrEmpty().not()) {
    Valid
  } else {
    invalid
  }
}

/**
 * A validator, which will return Valid for every input shorter than specified number of chars.
 * @param max maximum allowed number of chars
 * @param invalid a `Invalid` interface instance which will be returned on failure.
 */
public fun shorterThan(max: Int, invalid: Invalid = DefaultInvalid): Validator<String?> =
  Validator {
    if (it.orEmpty().length < max) {
      Valid
    } else {
      invalid
    }
  }

/**
 * A validator, which will return Valid for every input longer than specified number of chars.
 * @param min minimum allowed number of chars
 * @param invalid a `Invalid` interface instance which will be returned on failure.
 */
public fun longerThan(min: Int, invalid: Invalid = DefaultInvalid): Validator<String?> = Validator {
  if (it.orEmpty().length > min) {
    Valid
  } else {
    invalid
  }
}

/**
 * A validator, which will return Valid only if the size of the input will be exactly the provided size
 * @param length required length of the input
 * @param invalid a `Invalid` interface instance which will be returned on failure.
 */
public fun exactly(length: Int, invalid: Invalid = DefaultInvalid): Validator<String?> = Validator {
  if (it.orEmpty().length == length) {
    Valid
  } else {
    invalid
  }
}

/**
 * A validator, which will return Valid for every input matching the provided regex
 * @param regex a regex for matching the input
 * @param invalid a `Invalid` interface instance which will be returned on failure.
 */
public fun matches(regex: Regex, invalid: Invalid = DefaultInvalid): Validator<String?> =
  Validator {
    if (regex.matches(it.orEmpty())) {
      Valid
    } else {
      invalid
    }
  }

/**
 * A validator, which will return Valid for every non-null input
 * @param invalid a `Invalid` interface instance which will be returned on failure.
 */
public fun <T : Any?> notNull(invalid: Invalid = DefaultInvalid): Validator<T> = Validator {
  if (it != null) {
    Valid
  } else {
    invalid
  }
}

/**
 * A validator, which will return Valid for a `true` as the input
 * @param invalid a `Invalid` interface instance which will be returned on failure.
 */
public fun required(invalid: Invalid = DefaultInvalid): Validator<Boolean?> = Validator {
  if (it == true) {
    Valid
  } else {
    invalid
  }
}
