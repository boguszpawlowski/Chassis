package io.github.boguszpawlowski.chassis

import io.github.boguszpawlowski.chassis.ValidationStrategy.AsOptional

/**
 * A core component of a library. It represents a single field in your validation, it's value and validation state. It is also handling any updates to the
 * field itself. Note that you shouldn't use `update`, `forceValidation` functions on your own, as this are just a pure functions returning a new instance of the interface, and thus not updating the state.
 * Instead, you should use it's counterparts on a `Chassis` interface.
 */
public sealed class Field<T : Any, V> {
  /**
   * Current value of the field.
   */
  public abstract val value: V?

  /**
   * Whenever field is valid or not.
   */
  public abstract val isValid: Boolean

  /**
   * Whenever field is invalid or not.
   * Syntactic sugar over `invalidReasons.isNotEmpty()`
   */
  public abstract val isInvalid: Boolean

  /**
   * List of reasons for failed validation. If you force the validation, the invalid cause of it will be appended to this list.
   */
  public abstract val invalidReasons: List<Invalid>

  /**
   * A function for forcing the validation on the field. Use `forceValidation` of the chassis interface instead of it.
   * @return a new instance of the field
   * @param state current state of your model
   * @param validationResult validation result which you want to force
   */
  internal abstract fun forceValidation(state: T, validationResult: ValidationResult): T

  /**
   * A function for updating the fields value. Use `update` of the chassis interface instead of it.
   * @return new instance of the field
   */
  internal abstract fun reduce(state: T, newValue: V?): T

  /**
   * A function for re-triggering the validation without a change to the input value
   */
  internal abstract fun invalidate(state: T): T

  /**
   * A syntactic sugar for [resolve].
   */
  public abstract operator fun invoke(): V

  /**
   * A function that will return current value casted to fields type. Use it only after the validation is completed.
   */
  public abstract fun resolve(): V
}

internal data class FieldImpl<T : Any, V : Any?>(
  override val value: V? = null,
  private val validators: List<Validator<V?>>,
  private val validationStrategy: ValidationStrategy,
  private val reducer: Reducer<T, V>,
  private val forcedValidation: List<ValidationResult> = emptyList(),
  private val wasInvalidated: Boolean = false,
) : Field<T, V>() {

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
    val newField = copy(
      value = newValue,
      forcedValidation = emptyList(),
      wasInvalidated = false,
    )
    return reducer(state, newField)
  }

  override fun invalidate(state: T): T {
    val newField = copy(wasInvalidated = true)

    return reducer(state, newField)
  }

  override fun invoke(): V = resolve()

  @Suppress("UNCHECKED_CAST")
  override fun resolve(): V =
    if (validationStrategy == AsOptional) value as V else checkNotNull(value)

  private fun validate(value: V?): List<ValidationResult> =
    value.onNotNullOrInvalidated { notValidatedValue ->
      validators.map { it(notValidatedValue) }
    } ?: listOf(validationStrategy.fallback)

  private inline fun V?.onNotNullOrInvalidated(
    block: (V?) -> List<ValidationResult>,
  ): List<ValidationResult>? =
    if (this != null || this@FieldImpl.wasInvalidated) {
      block(this)
    } else {
      null
    }
}
