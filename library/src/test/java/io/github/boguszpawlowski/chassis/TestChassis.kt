package io.github.boguszpawlowski.chassis

@Suppress("LongParameterList")
internal fun testChassis(
  initialEmail: String? = null,
  initialLogin: String? = null,
  initialPassword: String? = null,
  initialMarketingConsent: Boolean? = null,
  initialPhoneNumber: String? = "123123123",
) = chassis<LoginForm> {
  LoginForm(
    email = field(initialEmail) {
      reducer { copy(email = it) }
    },
    login = field(initialLogin) {
      validators(notEmpty(TestInvalidCause))
      reducer { copy(login = it) }
    },
    password = field(initialPassword) {
      validators(longerThan(8, TestInvalidCause), matches("\\d+".toRegex(), TestInvalidCause))
      reducer { copy(password = it) }
    },
    marketingConsent = field(initialMarketingConsent) {
      reducer { copy(marketingConsent = it) }
    },
    phoneNumber = field(initialPhoneNumber) {
      validators(
        exactly(length = 9, invalid = TestInvalidCause),
        matches(regex = "\\d+".toRegex(), invalid = TestInvalidCause),
      )
      reducer { copy(phoneNumber = it) }
    }
  )
}

internal data class LoginForm(
  val email: Field<LoginForm, String>,
  val login: Field<LoginForm, String>,
  val password: Field<LoginForm, String>,
  val marketingConsent: Field<LoginForm, Boolean>,
  val phoneNumber: Field<LoginForm, String?>,
)

internal object TestInvalidCause : Invalid
