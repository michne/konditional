package io.amichne.kontracts.dsl

import io.amichne.kontracts.dsl.custom.CustomBooleanSchemaBuilder
import io.amichne.kontracts.dsl.custom.CustomDoubleSchemaBuilder
import io.amichne.kontracts.dsl.custom.CustomIntSchemaBuilder
import io.amichne.kontracts.dsl.custom.CustomStringSchemaBuilder
import kotlin.reflect.KProperty0

/**
 * Receiver-style alternative to `::property of { ... }` for non-null string properties.
 *
 * This avoids requiring context-parameter support at the call site:
 * `schema { of(::name) { minLength = 1 } }`
 */
@JvmName("rootObjectOfString")
@JsonSchemaBuilderDsl
fun RootObjectSchemaBuilder.of(
    property: KProperty0<String>,
    builder: StringSchemaBuilder.() -> Unit = {},
) {
    with(this) { property.of(builder) }
}

/**
 * Receiver-style alternative to `::property of { ... }` for nullable string properties.
 */
@JvmName("rootObjectOfNullableString")
@JsonSchemaBuilderDsl
fun RootObjectSchemaBuilder.of(
    property: KProperty0<String?>,
    builder: StringSchemaBuilder.() -> Unit = {},
) {
    with(this) { property.of(builder) }
}

/**
 * Receiver-style alternative to `::property of { ... }` for non-null boolean properties.
 */
@JvmName("rootObjectOfBoolean")
@JsonSchemaBuilderDsl
fun RootObjectSchemaBuilder.of(
    property: KProperty0<Boolean>,
    builder: BooleanSchemaBuilder.() -> Unit = {},
) {
    with(this) { property.of(builder) }
}

/**
 * Receiver-style alternative to `::property of { ... }` for nullable boolean properties.
 */
@JvmName("rootObjectOfNullableBoolean")
@JsonSchemaBuilderDsl
fun RootObjectSchemaBuilder.of(
    property: KProperty0<Boolean?>,
    builder: BooleanSchemaBuilder.() -> Unit = {},
) {
    with(this) { property.of(builder) }
}

/**
 * Receiver-style alternative to `::property of { ... }` for non-null integer properties.
 */
@JvmName("rootObjectOfInt")
@JsonSchemaBuilderDsl
fun RootObjectSchemaBuilder.of(
    property: KProperty0<Int>,
    builder: IntSchemaBuilder.() -> Unit = {},
) {
    with(this) { property.of(builder) }
}

/**
 * Receiver-style alternative to `::property of { ... }` for nullable integer properties.
 */
@JvmName("rootObjectOfNullableInt")
@JsonSchemaBuilderDsl
fun RootObjectSchemaBuilder.of(
    property: KProperty0<Int?>,
    builder: IntSchemaBuilder.() -> Unit = {},
) {
    with(this) { property.of(builder) }
}

/**
 * Receiver-style alternative to `::property of { ... }` for non-null double properties.
 */
@JvmName("rootObjectOfDouble")
@JsonSchemaBuilderDsl
fun RootObjectSchemaBuilder.of(
    property: KProperty0<Double>,
    builder: DoubleSchemaBuilder.() -> Unit = {},
) {
    with(this) { property.of(builder) }
}

/**
 * Receiver-style alternative to `::property of { ... }` for nullable double properties.
 */
@JvmName("rootObjectOfNullableDouble")
@JsonSchemaBuilderDsl
fun RootObjectSchemaBuilder.of(
    property: KProperty0<Double?>,
    builder: DoubleSchemaBuilder.() -> Unit = {},
) {
    with(this) { property.of(builder) }
}

/**
 * Receiver-style alternative to `::property of { ... }` for non-null object properties.
 */
@JvmName("rootObjectOfObject")
@JsonSchemaBuilderDsl
inline fun <reified V : Any> RootObjectSchemaBuilder.of(
    property: KProperty0<V>,
    noinline builder: ObjectSchemaBuilder.() -> Unit,
) {
    with(this) { property.of(builder) }
}

/**
 * Receiver-style alternative to `::property of { ... }` for nullable object properties.
 */
@JvmName("rootObjectOfNullableObject")
@JsonSchemaBuilderDsl
inline fun <reified V : Any> RootObjectSchemaBuilder.of(
    property: KProperty0<V?>,
    noinline builder: ObjectSchemaBuilder.() -> Unit = {},
) {
    with(this) { property.of(builder) }
}

/**
 * Receiver-style alternative to `::property asString { ... }` for non-null custom string mappings.
 */
@JvmName("rootObjectAsString")
@JsonSchemaBuilderDsl
inline fun <reified V : Any> RootObjectSchemaBuilder.asString(
    property: KProperty0<V>,
    noinline builder: CustomStringSchemaBuilder<V>.() -> Unit,
) {
    with(this) { property.asString(builder) }
}

/**
 * Receiver-style alternative to `::property asString { ... }` for nullable custom string mappings.
 */
@JvmName("rootObjectAsNullableString")
@JsonSchemaBuilderDsl
inline fun <reified V : Any> RootObjectSchemaBuilder.asString(
    property: KProperty0<V?>,
    noinline builder: CustomStringSchemaBuilder<V>.() -> Unit,
) {
    with(this) { property.asString(builder) }
}

/**
 * Receiver-style alternative to `::property asInt { ... }` for non-null custom integer mappings.
 */
@JvmName("rootObjectAsInt")
@JsonSchemaBuilderDsl
inline fun <reified V : Any> RootObjectSchemaBuilder.asInt(
    property: KProperty0<V>,
    noinline builder: CustomIntSchemaBuilder<V>.() -> Unit,
) {
    with(this) { property.asInt(builder) }
}

/**
 * Receiver-style alternative to `::property asInt { ... }` for nullable custom integer mappings.
 */
@JvmName("rootObjectAsNullableInt")
@JsonSchemaBuilderDsl
inline fun <reified V : Any> RootObjectSchemaBuilder.asInt(
    property: KProperty0<V?>,
    noinline builder: CustomIntSchemaBuilder<V>.() -> Unit,
) {
    with(this) { property.asInt(builder) }
}

/**
 * Receiver-style alternative to `::property asBoolean { ... }` for non-null custom boolean mappings.
 */
@JvmName("rootObjectAsBoolean")
@JsonSchemaBuilderDsl
inline fun <reified V : Any> RootObjectSchemaBuilder.asBoolean(
    property: KProperty0<V>,
    noinline builder: CustomBooleanSchemaBuilder<V>.() -> Unit,
) {
    with(this) { property.asBoolean(builder) }
}

/**
 * Receiver-style alternative to `::property asBoolean { ... }` for nullable custom boolean mappings.
 */
@JvmName("rootObjectAsNullableBoolean")
@JsonSchemaBuilderDsl
inline fun <reified V : Any> RootObjectSchemaBuilder.asBoolean(
    property: KProperty0<V?>,
    noinline builder: CustomBooleanSchemaBuilder<V>.() -> Unit,
) {
    with(this) { property.asBoolean(builder) }
}

/**
 * Receiver-style alternative to `::property asDouble { ... }` for non-null custom double mappings.
 */
@JvmName("rootObjectAsDouble")
@JsonSchemaBuilderDsl
inline fun <reified V : Any> RootObjectSchemaBuilder.asDouble(
    property: KProperty0<V>,
    noinline builder: CustomDoubleSchemaBuilder<V>.() -> Unit,
) {
    with(this) { property.asDouble(builder) }
}

/**
 * Receiver-style alternative to `::property asDouble { ... }` for nullable custom double mappings.
 */
@JvmName("rootObjectAsNullableDouble")
@JsonSchemaBuilderDsl
inline fun <reified V : Any> RootObjectSchemaBuilder.asDouble(
    property: KProperty0<V?>,
    noinline builder: CustomDoubleSchemaBuilder<V>.() -> Unit,
) {
    with(this) { property.asDouble(builder) }
}
