package io.github.boguszpawlowski.chassis

import app.cash.turbine.test
import io.kotest.assertions.asClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class ChassisTest : ShouldSpec({

  val chassis = chassis<LoginForm> {
    LoginForm(
      email = field {
        reducer { copy(email = it) }
      },
      login = field {
        validators(notEmpty(TestInvalidCause))
        reducer { copy(login = it) }
      },
      password = field {
        validators(longerThan(8, TestInvalidCause), matches("\\d+".toRegex(), TestInvalidCause))
        reducer { copy(password = it) }
      },
      marketingConsent = field {
        reducer { copy(marketingConsent = it) }
      }
    )
  }

  afterEach {
    chassis.reset()
  }

  context("chassis") {
    context("with no input data") {
      should("default its fields to null") {
        chassis.state.test {
          awaitItem().asClue {
            it.email.value shouldBe null
            it.login.value shouldBe null
            it.password.value shouldBe null
            it.marketingConsent.value shouldBe null
          }
        }
      }
      should("not be valid") {
        chassis.state.test {
          awaitItem().asClue {
            it.email.isValid shouldBe false
            it.login.isValid shouldBe false
            it.password.isValid shouldBe false
            it.marketingConsent.isValid shouldBe false
          }
        }
      }
      should("not be invalid") {
        chassis.state.test {
          awaitItem().asClue {
            it.email.isInvalid shouldBe false
            it.login.isInvalid shouldBe false
            it.password.isInvalid shouldBe false
            it.marketingConsent.isInvalid shouldBe false
          }
        }
      }
    }
    context("with no validators") {
      should("be valid on any input") {
        chassis.state.map { it.email }.test {
          awaitItem()
          chassis.update(LoginForm::email, "")
          awaitItem().isValid shouldBe true
          chassis.update(LoginForm::email, "23123")
          awaitItem().isValid shouldBe true
        }
      }
    }
    context("with an non-empty validator") {
      should("be invalid for empty input") {
        chassis.state.map { it.login }.test {
          awaitItem()
          chassis.update(LoginForm::login, "")
          awaitItem().asClue {
            it.isValid shouldBe false
            it.isInvalid shouldBe true
            it.invalidReasons shouldBe listOf(TestInvalidCause)
          }
        }
      }
      should("be valid on any non-empty input") {
        chassis.state.map { it.login }.test {
          awaitItem()
          chassis.update(LoginForm::login, "123")
          awaitItem().asClue {
            it.isValid shouldBe true
            it.isInvalid shouldBe false
            it.invalidReasons shouldBe emptyList()
          }
        }
      }
    }
    context("with multiple validators") {
      should("be invalid if one of validators is not fulfilled") {
        chassis.state.map { it.password }.test {
          awaitItem()
          chassis.update(LoginForm::password, "")
          awaitItem().asClue {
            it.isValid shouldBe false
            it.isInvalid shouldBe true
            it.invalidReasons shouldBe listOf(TestInvalidCause, TestInvalidCause)
          }
          chassis.update(LoginForm::password, "61938218318392")
          awaitItem().asClue {
            it.isValid shouldBe true
            it.isInvalid shouldBe false
            it.invalidReasons shouldBe emptyList()
          }
          chassis.update(LoginForm::password, "6482374893274923p")
          awaitItem().asClue {
            it.isValid shouldBe false
            it.isInvalid shouldBe true
            it.invalidReasons shouldBe listOf(TestInvalidCause)
          }
        }
      }
      should("respect forced validation until new value is present") {
        chassis.state.map { it.marketingConsent }.test {
          awaitItem()
          chassis.forceValidation(LoginForm::marketingConsent, TestInvalidCause)
          awaitItem().asClue {
            it.isValid shouldBe false
            it.isInvalid shouldBe true
            it.invalidReasons shouldBe listOf(TestInvalidCause)
          }
          chassis.update(LoginForm::marketingConsent, true)
          awaitItem().asClue {
            it.isValid shouldBe true
            it.isInvalid shouldBe false
            it.invalidReasons shouldBe emptyList()
          }
        }
      }
    }
  }
})

internal data class LoginForm(
  val email: Field<LoginForm, String>,
  val login: Field<LoginForm, String>,
  val password: Field<LoginForm, String>,
  val marketingConsent: Field<LoginForm, Boolean>,
)

internal object TestInvalidCause : Invalid
