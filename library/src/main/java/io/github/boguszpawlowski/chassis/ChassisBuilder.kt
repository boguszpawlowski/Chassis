package io.github.boguszpawlowski.chassis

public interface ChassisBuilderScope<T : Any> {
  public fun <V : Any> field(
    initialValue: V? = null,
    block: FieldBuilderScope<T, V>.() -> Unit
  ): Field<T, V>
}

@PublishedApi
internal class ChassisBuilder<T : Any> : ChassisBuilderScope<T> {
  override fun <V : Any> field(
    initialValue: V?,
    block: FieldBuilderScope<T, V>.() -> Unit
  ): Field<T, V> {
    val builder = FieldBuilder<T, V>(initialValue)
    builder.block()
    return builder.build()
  }
}
