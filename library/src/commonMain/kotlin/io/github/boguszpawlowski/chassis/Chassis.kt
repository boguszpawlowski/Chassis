package io.github.boguszpawlowski.chassis

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KProperty1

/**
 * Builder function for `Chassis` instance.
 * @param block function which should build a initial value of your form model.
 * @return a chassis instance, which will act as a manager for your form state.
 */
public inline fun <T : Any> chassis(block: ChassisBuilderScope<T>.() -> T): Chassis<T> =
  ChassisImpl(initialValue = ChassisBuilder<T>().block())

/**
 * An interface for form state manage.
 */
public interface Chassis<T : Any> {
  /**
   * StateFlow of form model which will emit new value everytime you modify any field of your model.
   */
  public val state: StateFlow<T>

  /**
   * A syntactic sugar over `state.value`.
   */
  public operator fun invoke(): T

  /**
   * A function for notifying chassis about any change to your form. Typically this would be bound to a callback of a view that represents a field's input.
   * E.g.:
   * emailTextView.doAfterTextChanged { newValue ->
   *  chassis.update(LoginForm::email, newValue)
   * }
   */
  public fun <V : Any?> update(field: KProperty1<T, Field<T, V>>, newValue: V)

  /**
   * A function for forcing a validation results independently from declared validators (e.g. validation on server).
   * Such validation result will be appended to the list of results from local validators and will be discarded on next update to the field.
   */
  public fun <V> forceValidation(
    field: KProperty1<T, Field<T, V>>,
    validationResult: ValidationResult,
  )

  /**
   * A function for forcing validation on current value of the field. It can be used to force validation, when the input hasn't changed, for example
   * when we loose focus on the text field.
   */
  public fun <V> invalidate(field: KProperty1<T, Field<T, V>>)

  /**
   * A function for resetting all fields to the initial values.
   */
  public fun reset()
}

@PublishedApi
internal class ChassisImpl<T : Any>(
  private val initialValue: T,
) : Chassis<T> {
  private val _state = MutableStateFlow(initialValue)
  override val state = _state

  override fun invoke() = state.value

  override fun <V : Any?> update(field: KProperty1<T, Field<T, V>>, newValue: V) {
    val newState = field.get(state.value).reduce(state.value, newValue)
    _state.value = newState
  }

  override fun <V : Any?> forceValidation(
    field: KProperty1<T, Field<T, V>>,
    validationResult: ValidationResult,
  ) {
    val newState = field.get(state.value).forceValidation(state.value, validationResult)
    _state.value = newState
  }

  override fun <V> invalidate(field: KProperty1<T, Field<T, V>>) {
    val newState = field.get(state.value).invalidate(state.value)
    _state.value = newState
  }

  override fun reset() {
    _state.value = initialValue
  }
}
