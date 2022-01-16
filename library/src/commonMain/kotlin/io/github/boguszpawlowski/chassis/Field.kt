package io.github.boguszpawlowski.chassis

import io.github.boguszpawlowski.chassis.ValidationStrategy.AsOptional

/**
 * A core component of a library. It represents a single field in your validation, it's value and validation state. It is also handling any updates to the
 * field itself. Note that you shouldn't use `update`, `forceValidation` functions on your own, as this are just a pure functions returning a new instance of the interface, and thus not updating the state.
 * Instead, you should use it's counterparts on a `Chassis` interface.
 */
public interface Field<T : Any, V : Any?> {
  /**
   * Current value of the field.
   */
  public val value: V?

  /**
   * Whenever field is valid or not.
   */
  public val isValid: Boolean

  /**
   * Whenever field is invalid or not.
   * Syntactic sugar over `invalidReasons.isNotEmpty()`
   */
  public val isInvalid: Boolean

  /**
   * List of reasons for failed validation. If you force the validation, the invalid cause of it will be appended to this list.
   */
  public val invalidReasons: List<Invalid>

  /**
   * An effectively internal function for forcing the validation on the field. Use `forceValidation` of the chassis interface instead of it.
   * @return a new instance of the field
   * @param state current state of your model
   * @param validationResult validation result which you want to force
   */
  public fun forceValidation(state: T, validationResult: ValidationResult): T

  /**
   * An effectively internal function for updating the fields value. Use `update` of the chassis interface instead of it.
   * @return new instance of the field
   */
  public fun reduce(state: T, newValue: V?): T

  /**
   * A function that will return current value casted to fields type. Use it only after the validation is completed.
   */
  public operator fun invoke(): V
}

internal data class FieldImpl<T : Any, V : Any?>(
  override val value: V? = null,
  private val validators: List<Validator<V>>,
  private val validationStrategy: ValidationStrategy,
  private val reducer: Reducer<T, V>,
  private val forcedValidation: List<ValidationResult> = emptyList()
) : Field<T, V> {

  private val validationResults: List<ValidationResult>
    get() = validate(value) + forcedValidation

  override val isValid: Boolean
    get() = validationResults.all { it is Valid }

  override val isInvalid: Boolean
    get() = validationResults.any { it is Invalid }

  override val invalidReasons: List<Invalid>
    get() = validationResults.filterIsInstance<Invalid>()

  override fun forceValidation(state: T, validationResult: ValidationResult): T {
    val newField = copy(forcedValidation = listOf(validationResult))
    return reducer.invoke(state, newField)
  }

  override fun reduce(state: T, newValue: V?): T {
    val newField = copy(value = newValue, forcedValidation = emptyList())
    return reducer(state, newField)
  }

  override fun invoke(): V =
    if (validationStrategy == AsOptional) value as V else checkNotNull(value)

  private fun validate(value: V?): List<ValidationResult> =
    value?.let { nonNullValue ->
      validators.map { it(nonNullValue) }
    } ?: listOf(validationStrategy.fallback)
}
