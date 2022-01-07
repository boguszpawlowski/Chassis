package io.github.boguszpawlowski.chassis

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

internal class FieldBuilderTest : ShouldSpec({

  val chassisBuilder = ChassisBuilder<LoginForm>()

  context("Field builder") {
    should("Map nullable type to optional field") {
      val field = with(chassisBuilder) {
        field<LoginForm, String?>(null) {
          reduce { copy(phoneNumber = it) }
        }
      }

      field() shouldBe null
    }
    should("Map non-nullable type to required field") {
      val field = with(chassisBuilder) {
        field<LoginForm, String>(null) {
          reduce { copy(email = it) }
        }
      }

      shouldThrow<IllegalStateException> {
        field()
      }
    }
  }
})
