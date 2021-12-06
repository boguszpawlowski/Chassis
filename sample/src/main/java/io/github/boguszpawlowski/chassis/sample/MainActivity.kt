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
import io.github.boguszpawlowski.chassis.FormModel
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
    val fields by viewModel.formData.state.collectAsState()

    Column {
      TextField(
        value = fields.email.value.orEmpty(),
        isError = fields.email.isInvalid,
        onValueChange = { viewModel.formData.update(FormData::email, it) }
      )
      Spacer(modifier = Modifier.height(10.dp))
      TextField(
        value = fields.login.value.orEmpty(),
        isError = fields.login.isInvalid,
        onValueChange = { viewModel.formData.update(FormData::login, it) },
      )
      Spacer(modifier = Modifier.height(10.dp))
      TextField(
        value = fields.password.value.orEmpty(),
        isError = fields.password.isInvalid,
        onValueChange = { viewModel.formData.update(FormData::password, it) }
      )
      Spacer(modifier = Modifier.height(10.dp))
      Button(
        enabled = fields.fieldsToBeValidated.all { it.isValid },
        onClick = { viewModel.onNext() }
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
  val formData = chassis<FormData> {
    FormData(
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

    formData.forceValidation(FormData::password, TooSimplePassword)
  }
}

object TooSimplePassword : Invalid

data class FormData(
  val login: Field<FormData, String>,
  val email: Field<FormData, String>,
  val password: Field<FormData, String>,
  val marketingConsent: Field<FormData, Boolean>,
) : FormModel {
  override val fieldsToBeValidated: Collection<Field<FormData, *>>
    get() = setOf(login, email, password)
}
