package io.github.boguszpawlowski.chassis

@PublishedApi
internal enum class ValidationStrategy(val fallback: ValidationResult) {
  AsOptional(Valid),
  AsRequired(Unspecified),
  ;
}
