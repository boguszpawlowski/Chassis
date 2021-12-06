package io.github.boguszpawlowski.chassis

typealias Reducer<T, V> = T.(Field<T, V>) -> T

interface FieldBuilderScope<T : FormModel, V : Any> {
  fun validators(vararg validators: Validator<V>)
  fun reducer(block: T.(Field<T, V>) -> T)
}

internal class FieldBuilder<T : FormModel, V : Any>(
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
