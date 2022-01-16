package io.github.boguszpawlowski.chassis

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

internal class ValidatorsTest : ShouldSpec({
  context("notEmpty") {
    val validator = notEmpty()
    should("return valid for non-empty value") {
      val validValue = "abc"

      validator(validValue) shouldBe Valid
    }
    should("return invalid for empty value") {
      val invalidValue = ""

      validator(invalidValue) shouldBe DefaultInvalid
    }
  }
  context("shorterThan") {
    val validator = shorterThan(6)
    should("return valid for shorter value") {
      val validValue = "123"

      validator(validValue) shouldBe Valid
    }
    should("return invalid for too long value") {
      val invalidValue = "1234567"

      validator(invalidValue) shouldBe DefaultInvalid
    }
  }
  context("longerThan") {
    val validator = longerThan(5)
    should("return valid for longer value") {
      val validValue = "123456"

      validator(validValue) shouldBe Valid
    }
    should("return invalid for too short value") {
      val invalidValue = "123"

      validator(invalidValue) shouldBe DefaultInvalid
    }
  }
  context("exactly") {
    val validator = exactly(4)
    should("return valid for value with exactly the length") {
      val validValue = "1234"

      validator(validValue) shouldBe Valid
    }
    should("return invalid for too long value") {
      val invalidValue = "12345"

      validator(invalidValue) shouldBe DefaultInvalid
    }
    should("return invalid for too short value") {
      val invalidValue = "123"

      validator(invalidValue) shouldBe DefaultInvalid
    }
  }
  context("matches") {
    val validator = matches("\\d+".toRegex())
    should("return valid for matching value") {
      val validValue = "123"

      validator(validValue) shouldBe Valid
    }
    should("return invalid for non-matching value") {
      val invalidValue = "abc"

      validator(invalidValue) shouldBe DefaultInvalid
    }
  }
  context("notNull") {
    val validator = notNull<String?>()
    should("return valid for not null value") {
      val validValue = "abc"

      validator(validValue) shouldBe Valid
    }
    should("return invalid for null value") {
      val invalidValue = null

      validator(invalidValue) shouldBe DefaultInvalid
    }
  }
  context("required") {
    val validator = required()
    should("return valid for true") {
      val validValue = true

      validator(validValue) shouldBe Valid
    }
    should("return invalid for false") {
      val invalidValue = false

      validator(invalidValue) shouldBe DefaultInvalid
    }
  }
  context("or") {
    val validator = shorterThan(2) or longerThan(10)
    should("return valid if first validator succeeds") {
      val validValue = "1"

      validator(validValue) shouldBe Valid
    }
    should("return valid if second validators succeeds") {
      val validValue = "12345678910"

      validator(validValue) shouldBe Valid
    }
    should("return invalid if both validators fail") {
      val invalidValue = "12345"

      validator(invalidValue) shouldBe DefaultInvalid
    }
  }
})
