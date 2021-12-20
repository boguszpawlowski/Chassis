package io.github.boguszpawlowski.chassis.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
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
import io.github.boguszpawlowski.chassis.notEmpty
import io.github.boguszpawlowski.chassis.notNull
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
    val form by viewModel.formData.state.collectAsState()

    Column {
      TextField(
        value = form.email.value.orEmpty(),
        isError = form.email.isInvalid,
        onValueChange = { viewModel.formData.update(LoginForm::email, it) },
      )
      Spacer(modifier = Modifier.height(10.dp))
      TextField(
        value = form.login.value.orEmpty(),
        isError = form.login.isInvalid,
        onValueChange = { viewModel.formData.update(LoginForm::login, it) },
      )
      Spacer(modifier = Modifier.height(10.dp))
      TextField(
        value = form.password.value.orEmpty(),
        isError = form.password.isInvalid,
        onValueChange = { viewModel.formData.update(LoginForm::password, it) },
      )
      Spacer(modifier = Modifier.height(10.dp))
      Button(
        enabled = form.isValid,
        onClick = { viewModel.onNext() },
      ) {
        Text(text = "Continue")
      }
    }
  }
}

class Register {
  suspend operator fun invoke(
    email: String,
    login: String,
    password: String,
    marketingConsent: Boolean,
  ): Result<Unit> = suspendCoroutine { it.resume(Result.success(Unit)) }
}

class MainViewModel(
  private val register: Register = Register()
) : ViewModel() {
  val formData = chassis<LoginForm> {
    LoginForm(
      email = field {
        validators(notEmpty())
        reducer { copy(email = it) }
      },
      login = field {
        validators(notEmpty())
        reducer { copy(login = it) }
      },
      password = field {
        validators(notEmpty())
        reducer { copy(password = it) }
      },
      marketingConsent = field(initialValue = false) {
        validators(notNull())
        reducer { copy(marketingConsent = it) }
      },
    )
  }

  fun onNext() = viewModelScope.launch {
    with(formData()) {
      register(
        email = email(),
        login = login(),
        password = password(),
        marketingConsent = marketingConsent(),
      )
    }

    formData.forceValidation(LoginForm::password, TooSimplePassword)
  }
}

object TooSimplePassword : Invalid

data class LoginForm(
  val login: Field<LoginForm, String>,
  val email: Field<LoginForm, String>,
  val password: Field<LoginForm, String>,
  val marketingConsent: Field<LoginForm, Boolean>,
) {
  val isValid: Boolean get() = login.isValid && email.isValid && password.isValid && marketingConsent.isValid
}
