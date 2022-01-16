package io.github.boguszpawlowski.chassis

import io.github.boguszpawlowski.chassis.ValidationStrategy.AsOptional
import io.github.boguszpawlowski.chassis.ValidationStrategy.AsRequired

@ChassisDslMarker
public interface ChassisBuilderScope<T : Any>

/**
 * A builder function which will accept a function declaring the field of the form model. To have a access to this function,
 * you have to use `chassis` function first.
 */
public inline fun <T : Any, reified V : Any?> ChassisBuilderScope<T>.field(
  initialValue: V? = null,
  block: FieldBuilderScope<T, V>.() -> Reducer<T, V>,
): Field<T, V> {
  val builder = FieldBuilder<T, V>(
    initialValue = initialValue,
    validationStrategy = if (isNullable<V>()) AsOptional else AsRequired,
  )
  val reducer = builder.block()
  return builder.build(reducer)
}

@PublishedApi
internal class ChassisBuilder<T : Any> : ChassisBuilderScope<T>

@PublishedApi
internal inline fun <reified T> isNullable(): Boolean = null is T
