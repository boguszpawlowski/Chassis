package io.github.boguszpawlowski.chassis.sample

import android.os.Bundle
import android.util.Patterns
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.boguszpawlowski.chassis.Field
import io.github.boguszpawlowski.chassis.Invalid
import io.github.boguszpawlowski.chassis.chassis
import io.github.boguszpawlowski.chassis.exactly
import io.github.boguszpawlowski.chassis.field
import io.github.boguszpawlowski.chassis.longerThan
import io.github.boguszpawlowski.chassis.matches
import io.github.boguszpawlowski.chassis.notEmpty
import io.github.boguszpawlowski.chassis.reduce
import io.github.boguszpawlowski.chassis.required
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MainScreen()
    }
  }
}

@Composable
fun MainScreen(viewModel: MainViewModel = MainViewModel()) {
  MaterialTheme {
    val form by viewModel.chassis.state.collectAsState()

    Column {
      TextField(
        value = form.email.value.orEmpty(),
        isError = form.email.isInvalid,
        label = { Text(text = "Email") },
        onValueChange = { viewModel.chassis.update(LoginForm::email, it) },
      )
      Spacer(modifier = Modifier.height(10.dp))
      TextField(
        value = form.login.value.orEmpty(),
        isError = form.login.isInvalid,
        label = { Text(text = "Login") },
        onValueChange = { viewModel.chassis.update(LoginForm::login, it) },
      )
      Spacer(modifier = Modifier.height(10.dp))
      TextField(
        value = form.password.value.orEmpty(),
        isError = form.password.isInvalid,
        label = { Text(text = "Password") },
        onValueChange = { viewModel.chassis.update(LoginForm::password, it) },
      )
      Spacer(modifier = Modifier.height(10.dp))
      TextField(
        value = form.phoneNumber.value.orEmpty(),
        isError = form.phoneNumber.isInvalid,
        label = { Text(text = "Phone Number (optional)") },
        onValueChange = {
          viewModel.chassis.update(
            field = LoginForm::phoneNumber,
            newValue = it.takeUnless { it.isEmpty() },
          )
        },
      )
      Spacer(modifier = Modifier.height(10.dp))
      Checkbox(
        checked = form.marketingConsent.value ?: false,
        onCheckedChange = { viewModel.chassis.update(LoginForm::marketingConsent, it) },
      )
      Spacer(modifier = Modifier.height(10.dp))
      Button(
        enabled = form.isValid,
        onClick = { viewModel.onNext() },
      ) {
        Text(text = "Continue")
      }
      Spacer(modifier = Modifier.height(10.dp))
      Button(
        onClick = { viewModel.chassis.reset() },
      ) {
        Text(text = "Reset")
      }
    }
  }
}

class Register {
  @Suppress("LongParameterList")
  suspend operator fun invoke(
    email: String,
    login: String,
    password: String,
    marketingConsent: Boolean,
    phoneNumber: String?,
  ): Result<Unit> = suspendCoroutine { it.resume(Result.success(Unit)) }
}

class MainViewModel(
  private val register: Register = Register(),
) : ViewModel() {

  val chassis = chassis<LoginForm> {
    LoginForm(
      email = field {
        validators(notEmpty(), matches(Patterns.EMAIL_ADDRESS.toRegex()))
        reduce { copy(email = it) }
      },
      login = field {
        validators(notEmpty())
        reduce { copy(login = it) }
      },
      password = field {
        validators(notEmpty(), longerThan(8))
        reduce { copy(password = it) }
      },
      marketingConsent = field {
        validators(required())
        reduce { copy(marketingConsent = it) }
      },
      phoneNumber = field {
        validators(exactly(9), matches("\\d+".toRegex()))
        reduce { copy(phoneNumber = it) }
      }
    )
  }

  fun onNext() = viewModelScope.launch {
    with(chassis()) {
      register(
        email = email(),
        login = login(),
        password = password(),
        marketingConsent = marketingConsent(),
        phoneNumber = phoneNumber(),
      )
    }

    chassis.forceValidation(LoginForm::password, TooSimplePassword)
  }
}

object TooSimplePassword : Invalid

data class LoginForm(
  val login: Field<LoginForm, String>,
  val email: Field<LoginForm, String>,
  val password: Field<LoginForm, String>,
  val marketingConsent: Field<LoginForm, Boolean>,
  val phoneNumber: Field<LoginForm, String?>,
) {
  val isValid: Boolean
    @Suppress("MaxLineLength")
    get() = login.isValid && email.isValid && password.isValid && marketingConsent.isValid && phoneNumber.isValid
}
