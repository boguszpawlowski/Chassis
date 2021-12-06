package io.github.boguszpawlowski.chassis

interface Field<T : FormModel, V : Any> {
  val value: V?
  val isValid: Boolean
  val isInvalid: Boolean
  val invalidReasons: List<Invalid>
  fun forceValidation(state: T, validationResult: ValidationResult): T
  fun reduce(state: T, newValue: V): T
  operator fun invoke(): V
}

internal data class FieldImpl<T : FormModel, V : Any>(
  override val value: V? = null,
  private val validators: List<Validator<V>>,
  private val reducer: Reducer<T, V>,
  private val forcedValidation: List<ValidationResult> = emptyList()
) : Field<T, V> {

  private val validationResults: List<ValidationResult>
    get() = (value?.let { validators.validate(it) } ?: listOf(Unspecified)) + forcedValidation

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

  override fun reduce(state: T, newValue: V): T {
    val newField = copy(value = newValue, forcedValidation = emptyList())
    return reducer.invoke(state, newField)
  }

  override fun invoke(): V = requireNotNull(value)
}

fun <T> Collection<Validator<T>>.validate(input: T) = map { it(input) }
