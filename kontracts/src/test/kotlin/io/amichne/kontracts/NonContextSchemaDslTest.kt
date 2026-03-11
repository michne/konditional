package io.amichne.kontracts

import io.amichne.kontracts.dsl.asBoolean
import io.amichne.kontracts.dsl.asDouble
import io.amichne.kontracts.dsl.asInt
import io.amichne.kontracts.dsl.asString
import io.amichne.kontracts.dsl.of
import io.amichne.kontracts.dsl.schema
import io.amichne.kontracts.schema.BooleanSchema
import io.amichne.kontracts.schema.DoubleSchema
import io.amichne.kontracts.schema.IntSchema
import io.amichne.kontracts.schema.ObjectSchema
import io.amichne.kontracts.schema.SchemaProvider
import io.amichne.kontracts.schema.StringSchema
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class NonContextSchemaDslTest {
    data class NestedConfig(
        val path: String,
    )

    data class PrimitiveConfig(
        val name: String,
        val nickname: String?,
        val enabled: Boolean,
        val verified: Boolean?,
        val count: Int,
        val retries: Int?,
        val ratio: Double,
        val threshold: Double?,
        val nested: NestedConfig,
        val optionalNested: NestedConfig?,
    ) : SchemaProvider<ObjectSchema> {
        override val schema = schema {
            of(::name) {
                minLength = 1
                description = "Display name"
            }
            of(::nickname) {
                description = "Optional nickname"
            }
            of(::enabled) {
                default = false
            }
            of(::verified) {
                description = "Optional verification"
            }
            of(::count) {
                minimum = 0
            }
            of(::retries) {
                minimum = 0
            }
            of(::ratio) {
                maximum = 1.0
            }
            of(::threshold) {
                minimum = 0.0
            }
            of(::nested) {
                description = "Nested object"
            }
            of(::optionalNested) {
                description = "Optional nested object"
            }
        }
    }

    data class UserId(
        val value: String,
    )

    data class AttemptCount(
        val value: Int,
    )

    data class AgentFlag(
        val value: Boolean,
    )

    data class CompletionRate(
        val value: Double,
    )

    data class CustomConfig(
        val userId: UserId,
        val optionalUserId: UserId?,
        val attempts: AttemptCount,
        val optionalAttempts: AttemptCount?,
        val agent: AgentFlag,
        val optionalAgent: AgentFlag?,
        val completion: CompletionRate,
        val optionalCompletion: CompletionRate?,
    ) : SchemaProvider<ObjectSchema> {
        override val schema = schema {
            asString(::userId) {
                represent = { value }
                minLength = 8
            }
            asString(::optionalUserId) {
                represent = { value }
                description = "Optional user id"
            }
            asInt(::attempts) {
                represent = { value }
                minimum = 0
            }
            asInt(::optionalAttempts) {
                represent = { value }
                description = "Optional attempts"
            }
            asBoolean(::agent) {
                represent = { value }
                description = "Agent flag"
            }
            asBoolean(::optionalAgent) {
                represent = { value }
                description = "Optional agent flag"
            }
            asDouble(::completion) {
                represent = { value }
                maximum = 100.0
            }
            asDouble(::optionalCompletion) {
                represent = { value }
                description = "Optional completion"
            }
        }
    }

    @Test
    fun `receiver-style of alternatives cover primitive nullable and object properties`() {
        val config = PrimitiveConfig(
            name = "alice",
            nickname = null,
            enabled = true,
            verified = null,
            count = 1,
            retries = null,
            ratio = 0.5,
            threshold = null,
            nested = NestedConfig(path = "/a"),
            optionalNested = null,
        )

        val fields = config.schema.fields

        assertIs<StringSchema>(fields.getValue("name").schema)
        assertTrue(fields.getValue("name").required)
        assertEquals("Display name", fields.getValue("name").schema.description)

        assertIs<StringSchema>(fields.getValue("nickname").schema)
        assertFalse(fields.getValue("nickname").required)
        assertTrue((fields.getValue("nickname").schema as StringSchema).nullable)

        assertIs<BooleanSchema>(fields.getValue("enabled").schema)
        assertTrue(fields.getValue("enabled").required)

        assertIs<BooleanSchema>(fields.getValue("verified").schema)
        assertFalse(fields.getValue("verified").required)

        assertIs<IntSchema>(fields.getValue("count").schema)
        assertTrue(fields.getValue("count").required)

        assertIs<IntSchema>(fields.getValue("retries").schema)
        assertFalse(fields.getValue("retries").required)

        assertIs<DoubleSchema>(fields.getValue("ratio").schema)
        assertTrue(fields.getValue("ratio").required)

        assertIs<DoubleSchema>(fields.getValue("threshold").schema)
        assertFalse(fields.getValue("threshold").required)

        assertIs<ObjectSchema>(fields.getValue("nested").schema)
        assertTrue(fields.getValue("nested").required)

        assertIs<ObjectSchema>(fields.getValue("optionalNested").schema)
        assertFalse(fields.getValue("optionalNested").required)
    }

    @Test
    fun `receiver-style custom mapping alternatives cover all custom mapping families`() {
        val config = CustomConfig(
            userId = UserId("ABCDEFGH"),
            optionalUserId = null,
            attempts = AttemptCount(1),
            optionalAttempts = null,
            agent = AgentFlag(true),
            optionalAgent = null,
            completion = CompletionRate(42.0),
            optionalCompletion = null,
        )

        val fields = config.schema.fields

        assertIs<StringSchema>(fields.getValue("userId").schema)
        assertTrue(fields.getValue("userId").required)

        assertIs<StringSchema>(fields.getValue("optionalUserId").schema)
        assertFalse(fields.getValue("optionalUserId").required)
        assertTrue((fields.getValue("optionalUserId").schema as StringSchema).nullable)

        assertIs<IntSchema>(fields.getValue("attempts").schema)
        assertTrue(fields.getValue("attempts").required)

        assertIs<IntSchema>(fields.getValue("optionalAttempts").schema)
        assertFalse(fields.getValue("optionalAttempts").required)
        assertTrue((fields.getValue("optionalAttempts").schema as IntSchema).nullable)

        assertIs<BooleanSchema>(fields.getValue("agent").schema)
        assertTrue(fields.getValue("agent").required)

        assertIs<BooleanSchema>(fields.getValue("optionalAgent").schema)
        assertFalse(fields.getValue("optionalAgent").required)
        assertTrue((fields.getValue("optionalAgent").schema as BooleanSchema).nullable)

        assertIs<DoubleSchema>(fields.getValue("completion").schema)
        assertTrue(fields.getValue("completion").required)

        assertIs<DoubleSchema>(fields.getValue("optionalCompletion").schema)
        assertFalse(fields.getValue("optionalCompletion").required)
        assertTrue((fields.getValue("optionalCompletion").schema as DoubleSchema).nullable)
    }
}
