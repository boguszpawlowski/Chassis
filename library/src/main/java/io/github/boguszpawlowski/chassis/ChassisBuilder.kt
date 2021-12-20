package io.github.boguszpawlowski.chassis

public interface ChassisBuilderScope<T : Any>

@PublishedApi
internal class ChassisBuilder<T : Any> : ChassisBuilderScope<T>

public inline fun <T : Any, V : Any> ChassisBuilderScope<T>.field(
  initialValue: V? = null,
  block: FieldBuilderScope<T, V>.() -> Unit
): Field<T, V> {
  val builder = FieldBuilder<T, V>(initialValue)
  builder.block()
  return builder.build()
}
