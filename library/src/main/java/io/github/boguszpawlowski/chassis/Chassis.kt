package io.github.boguszpawlowski.chassis

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KProperty1

// TODO depenedencies
// TODO side effects

interface FormModel {
  val fieldsToBeValidated: Collection<Field<*, *>>
}

inline fun <T : FormModel> chassis(crossinline block: ChassisBuilderScope<T>.() -> T): Chassis<T> =
  ChassisImpl(initialValue = { ChassisBuilder<T>().block() })

interface Chassis<T : FormModel> {
  val state: StateFlow<T>
  operator fun invoke(): T
  fun <V : Any> update(field: KProperty1<T, Field<T, V>>, newValue: V)
  fun <V : Any> forceValidation(
    field: KProperty1<T, Field<T, V>>,
    validationResult: ValidationResult
  )
}

@PublishedApi
internal class ChassisImpl<T : FormModel>(initialValue: () -> T) : Chassis<T> {
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

fun main() {
  val chassis = chassis<LoginForm> {
    LoginForm(
      login = field {
        validators(notEmpty(), shorterThan(8))
        reducer { newLogin ->
          copy(login = newLogin)
        }
      },
      password = field {
        validators(notEmpty())
        reducer { newPassword ->
          copy(password = newPassword)
        }
      },
      marketingConsent = field {
        validators(notNull())
        reducer { marketingConsent ->
          copy(marketingConsent = marketingConsent)
        }
      }
    )
  }

  chassis.state.onEach { }
}

data class LoginForm(
  val login: Field<LoginForm, String>,
  val password: Field<LoginForm, String>,
  val marketingConsent: Field<LoginForm, Boolean>,
) : FormModel {
  override val fieldsToBeValidated: Collection<Field<*, *>>
    get() = setOf(login, password, marketingConsent)
}
