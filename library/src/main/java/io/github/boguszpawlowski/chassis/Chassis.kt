package io.github.boguszpawlowski.chassis

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KProperty1

public inline fun <T : Any> chassis(crossinline block: ChassisBuilderScope<T>.() -> T): Chassis<T> =
  ChassisImpl(initialValue = { ChassisBuilder<T>().block() })

public interface Chassis<T : Any> {
  public val state: StateFlow<T>
  public operator fun invoke(): T
  public fun <V : Any> update(field: KProperty1<T, Field<T, V>>, newValue: V)
  public fun <V : Any> forceValidation(
    field: KProperty1<T, Field<T, V>>,
    validationResult: ValidationResult
  )
}

@PublishedApi
internal class ChassisImpl<T : Any>(initialValue: () -> T) : Chassis<T> {
  private val _state = MutableStateFlow(initialValue())
  override val state = _state

  override fun invoke() = state.value

  override fun <V : Any> update(field: KProperty1<T, Field<T, V>>, newValue: V) {
    val newState = field.get(state.value).reduce(state.value, newValue)
    _state.value = newState
  }

  override fun <V : Any> forceValidation(
    field: KProperty1<T, Field<T, V>>,
    validationResult: ValidationResult
  ) {
    val newState = field.get(state.value).forceValidation(state.value, validationResult)
    _state.value = newState
  }
}
