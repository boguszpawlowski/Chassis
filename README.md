# Chassis
A lightweight Kotlin library for a form state management and field validation.

![Github Actions](https://github.com/boguszpawlowski/chassis/actions/workflows/check.yml/badge.svg)
[<img src="https://img.shields.io/maven-central/v/io.github.boguszpawlowski.chassis/chassis.svg?label=release%20version"/>](https://search.maven.org/search?q=g:io.github.boguszpawlowski.chassis)
[<img src="https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/io.github.boguszpawlowski.chassis/chassis.svg?label=snapshot%20version"/>](https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/boguszpawlowski/chassis/)

## Setup
Library and it's snapshots are available on Maven Central repository.
```kotlin
  // module-level build.gradle
  dependencies {
    implementation "io.github.boguszpawlowski.chassis:chassis:<latest-version>"
  }
```

## The problem
Dealing with complex form logic is a standard task for almost any modern application. When dealing with multiple inputs and outputs it is hard to keep the code clean, concise and consistent across the project.
Chassis is a library that tries to solve this problems by offering a lightweight dsl for building your form data, managing it state and propagating the changes to the UI, as well as enabling you to compose your own validation logic.

## Concept
The Chassis is a very simple concept. At its core its just a `StateFlow` of the class representing your form data, with an additional functions for updating the fields and peeking the value.

## Advantages

- Dealing with nullability - your form model can accept null values as initial data, but the library will make sure that after validation, you can access non-nullable values, thus saving you the cumbersome process of null checking.
- Unopinionated - the library is pure Kotlin (currently only targeting JVM), thus you can use any view system you need.
- Lightweight - the only dependency is the core coroutine library.
- Conciseness - traditionally, when dealing with forms, you code can be quickly polluted by `updateLogin()`, `updateEmail()` etc. functions. The library has only one function for updating the state, leveraging `property reference` feature of Kotlin. 
- Support for async validation  
- Build with declarative paradigm in mind.

## Usage Guide

### Basic example 
We are going to build basic login form with client-site validation. For the view system we are going to use Jetpack Compose, but you can use any system you need.
If you want to go straight to the code, you can check out the [Basic Sample](sample/src/main/java/io/github/boguszpawlowski/chassis/sample/BasicSample.kt) file.

### 1. Model your form data. 
The recommended way is to use Kotlin's data class. In case you don't use it, make sure you have implemented `equals` function. Every field of the form should be represented by a `Field<T, V>` interface where T is the type of the model and V - the type of the data in the field (e.g. `String` for the email, `Boolean` for some kind of consent).
```kotlin
  data class LoginForm(
    val login: Field<LoginForm, String>,
    val password: Field<LoginForm, String>,
  )
```
> :exclamation: Since `Chassis` uses a `StateFlow` underneath it is critical to implement a valid equals for your function, due to [equality-based conflation](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/).
### 2. Use DSL to make a chassis instance.

The DSL consists of 4 components:

### Chassis
The Chassis interface is a sort of manager for your form model. It consists of:
- `state` property - a `StateFlow<T>` with current value of your form model.
- `update` - a function for updating any field of the form, by passing an property reference (e.g. `LoginForm::login`) and the new value.
- `invoke` - a function for returning a current value of the form model - it can be used for getting the data after form submit, or just too peek the value (it's just a syntactic sugar for `Chassis.state.value` )
- `forceValidation` - a function for forcing the validation result of a field [Async validation](#async-validation).
- `reset` - a function for resetting all fields to the initial values.

To create an instance of the `Chassis`, use the `chassis` builder function. It accepts value of the type you have provided as a representation of the form data.
```kotlin
  val loginForm = chassis<LoginForm> {
    LoginForm(
      login = TODO(),
      password = TODO(),
    )
  }
```
### Field
The `Field` interface is the core component of the library. It consists of:
- `value` property - current value of the field.
- `isValid` - whenever the field is valid or not.
- `isInvalid` - whenever the field is invalid or not.
- `invalidReasons` - list of all failed validation results.
- `invoke` function - it returns a current value of the field - it can be used for getting the data after form submit.
> :exclamation: 'invoke()' function calls a null-assertion (or cast for nullable fields), so you can only call it once you know the data is valid.

To create an instance of the `Field` use the `field` builder function. It accepts a lambda with the `FieldBuilderScope` receivers, which provides a `validators` and `reduce` functions.
```kotlin
  ...
  login = field { TODO() }
  ...
```
### Validators
The field builder function provides a `validators()` function, which accepts any number of `Validator` instances. Validator is a functional type, that accepts a value of type `T` and returns an instance of `ValidationResult` interface. You can create your own implementation of it, or use some of the validators provided by library. For more information check out the `Validation` section.
```kotlin
  ...
  login = field { 
    validators(notEmpty(), longerThan(6))
    TODO() 
  }
  ...
```
### Reducer
The `field` builder function requires from you to return an instance of `Reducer` interface, which will describe how to modify the model after an field update. The easiest way to provide it is to use `reduce` higher-order function, which passes current model as a receiver
```kotlin
  typealias Reducer<T, V> = T.(Field<T, V>) -> T
```
The suggested way to reduce the model is to leverage data class `copy` function:
```kotlin
  ...
  login = field {
    validators(notEmpty(), longerThan(6))
    reduce { copy(login = it) }
  }
  ...
```
### 3. Use `invoke()` functions to access the data after submit.
```kotlin
  with(loginForm()) { // using invoke function to access current state
    val loginValue: String = login()
    val emailValue: String = email()
  }
```

### Full example
```kotlin
  class MainViewModel(private val register: RegisterUseCase) {
    val loginForm = chassis<LoginForm> {
      LoginForm(
        login = field {
          validators(notEmpty(), longerThan(6))
          reduce { copy(login = it) }
        },
        password = field {
          validators(notEmpty(), longerThan(8))
          reduce { copy(password = it) }
        },
      )
    }
  
    fun onSubmit() {
      with(loginForm()) {
        register(login(), password())
      }
    }
  }

  @Composable
  fun MainScreen(mainViewModel: MainViewModel = MainViewModel()) {
    val form = mainViewModel.loginForm.state.collectAsState()
    Column {
      TextField(
        value = form.login.value.orEmpty(),
        isError = form.login.isInvalid,
        onValueChange = { viewModel.chassis.update(LoginForm::login, it) },
      )
      TextField(
        value = form.password.value.orEmpty(),
        isError = form.password.isInvalid,
        onValueChange = { viewModel.chassis.update(LoginForm::password, it) },
      )
      Button(
        enabled = { form.login.isValid && form.password.isValid },
        onClick = { viewModel.onSubmit() },
      ) {
        Text(text = "Submit")
      }
    }
  }
```

### Validation

You can see that every Field has 2 properties that are determining validation state: `isValid` and `isInvalid`. 
When the user haven't provided any input, the required fields won't be valid nor invalid - they will be in so called `Unspecified` state. 
This mechanism is there to prevent your UI from showing error before any user input.
For showing a specific error message yous should use `invalidReasons` property, which consists all failed validation results of your field.

### Configuring your validation rules
The basic element of validation configuration is an `Validator` interface. It's basically a function, that accepts current value and returns an instance of `ValidationResult` sealed interface.
The validation result can either be `Valid` - concrete object representing correct value, `Unspecified` - concrete object being a default result for a required field, before any user input or `Invalid` - an interface representing failed validation.
Due to `Invalid` being an interface, you can return your own implementation of it, which you will be able to map to the specific message in the view layer.

```kotlin

  // your own invalid reason
  data class TooLongInput(val maxLength: Int): Invalid

  // your validator
  fun hasMaxLength(maxLength: Int) = Validator<String?> { value ->
    if (value.orEmpty().size >= maxLength) {
      TooLongInput(maxLength)
    } else {
      Valid
    }
  }

  // pass your validators into the field declaration
  ...
  login = field {
    validators(hasMaxLength(9))
    ...
  }
  ...
```

### Validators composition
If you pass multiple validators into the field declaration, failing only one of them will make whole field invalid. If you want to have field valid when it passes any of the declared validators, 
you should use `or` function, which creates a validator, which will be valid even if only one of the combined validators passes.

## Advanced Features

### Optional Fields
By default all the declared fields will be required - if there is no input they will be invalid. If you want to declare a field that can be optional, you should pass a nullable type in a field declaration.
```kotlin
  data class LoginForm(
    val login: Field<LoginForm, String>, // required
    val password: Field<LoginForm, String>, // required
    val phoneNumber: Field<LoginForm, String?> // optional
  )
```
Such a field will be valid on no input or nullable input.

> :exclamation: When working with text fields you have to remember to not allow for a empty string input for a nullable field. If you don't do that, after user inputs a text and then removes it, the field will always be invalid.
> E.g. in Jetpack Compose:
```kotlin
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
```

### Async Validation
The common scenario when dealing with forms is server side validation. For this purpose the concept of `Forced Validation` is introduced. By using this mechanism you can add validation result completely independently of validators in field declaration. 

```kotlin

  val loginForm = chassis<LoginForm> { ... }

  suspend fun validateEmail(email: String) : ValidationResult = api.validateEmail(email)

  fun onSubmitClicked() {
    val emailValidationResult = validateEmail(loginForm().email())
    
    loginForm.forceValidation(LoginForm::email, emailValidationResult)
  }
```
Such result will be appended to the list of the invalid reasons (if it's invalid) and it will be only present until the next update to the field's value.

## License

    Copyright 2022 Bogusz Pawłowski

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
