package io.github.boguszpawlowski.chassis

public typealias Reducer<T, V> = T.(Field<T, V>) -> T

@ChassisDslMarker
public interface FieldBuilderScope<T : Any, V : Any?> {
  public fun validators(vararg validators: Validator<V>)
}

public fun <T : Any, V : Any?> FieldBuilderScope<T, V>.reducer(reducer: Reducer<T, V>): Reducer<T, V> =
  reducer

@PublishedApi
internal class FieldBuilder<T : Any, V : Any?>(
  private val isOptional: Boolean,
  private val initialValue: V?,
) : FieldBuilderScope<T, V> {
  private val validators = arrayListOf<Validator<V>>()

  override fun validators(vararg validators: Validator<V>) {
    this.validators.addAll(validators)
  }

  fun build(reducer: Reducer<T, V>) = FieldImpl(initialValue, validators, reducer, isOptional)
}
