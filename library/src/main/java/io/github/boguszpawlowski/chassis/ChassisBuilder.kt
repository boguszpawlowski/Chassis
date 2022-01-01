package io.github.boguszpawlowski.chassis

@ChassisDslMarker
public interface ChassisBuilderScope<T : Any>

@PublishedApi
internal class ChassisBuilder<T : Any> : ChassisBuilderScope<T>

public inline fun <T : Any, reified V : Any?> ChassisBuilderScope<T>.field(
  initialValue: V? = null,
  block: FieldBuilderScope<T, V>.() -> Reducer<T, V>,
): Field<T, V> {
  val builder = FieldBuilder<T, V>(
    isOptional = isNullable<V>(),
    initialValue = initialValue,
  )
  val reducer = builder.block()
  return builder.build(reducer)
}

@PublishedApi
internal inline fun <reified T> isNullable(): Boolean = null is T
