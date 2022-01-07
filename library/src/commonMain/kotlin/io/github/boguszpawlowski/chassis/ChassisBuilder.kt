package io.github.boguszpawlowski.chassis

import io.github.boguszpawlowski.chassis.ValidationStrategy.AsOptional
import io.github.boguszpawlowski.chassis.ValidationStrategy.AsRequired

@ChassisDslMarker
public interface ChassisBuilderScope<T : Any>

@PublishedApi
internal class ChassisBuilder<T : Any> : ChassisBuilderScope<T>

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
internal inline fun <reified T> isNullable(): Boolean = null is T
