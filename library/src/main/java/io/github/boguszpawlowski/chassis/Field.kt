package io.github.boguszpawlowski.chassis

public interface Field<T : Any, V : Any?> {
  public val value: V?
  public val isValid: Boolean
  public val isInvalid: Boolean
  public val invalidReasons: List<Invalid>
  public fun forceValidation(state: T, validationResult: ValidationResult): T
  public fun reduce(state: T, newValue: V?): T
  public operator fun invoke(): V
}

internal data class FieldImpl<T : Any, V : Any?>(
  override val value: V? = null,
  private val validators: List<Validator<V>>,
  private val reducer: Reducer<T, V>,
  private val forcedValidation: List<ValidationResult> = emptyList()
) : Field<T, V> {

  private val validationResults: List<ValidationResult>
    get() = (value.onCorrectType(::validate) ?: listOf(Unspecified)) + forcedValidation

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

  override fun invoke(): V = value as V

  private fun validate(value: V) = validators.map { it(value) }

  private inline fun <R> V?.onCorrectType(block: (V) -> R): R? = (this as? V)?.let(block)
}
