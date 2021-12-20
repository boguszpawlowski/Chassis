package io.github.boguszpawlowski.chassis

public typealias Reducer<T, V> = T.(Field<T, V>) -> T

public interface FieldBuilderScope<T : Any, V : Any> {
  public fun validators(vararg validators: Validator<V>)
  public fun reducer(block: T.(Field<T, V>) -> T)
}

internal class FieldBuilder<T : Any, V : Any>(
  private val initialValue: V?,
) : FieldBuilderScope<T, V> {
  private val validators = arrayListOf<Validator<V>>()
  private var reducer: Reducer<T, V> = { this }

  override fun validators(vararg validators: Validator<V>) {
    this.validators.addAll(validators)
  }

  override fun reducer(block: T.(Field<T, V>) -> T) {
    reducer = block
  }

  fun build() = FieldImpl(initialValue, validators, reducer)
}
