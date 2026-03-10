package io.amichne.konditional.core.types

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KonstrainedDefaultsTest {
    private data class DefaultSchemaConfig(
        val enabled: Boolean,
    ) : Konstrained.Object

    @Test
    fun `konstrained object remains in the object hierarchy`() {
        assertTrue(Konstrained.Object::class.java.isInstance(DefaultSchemaConfig(enabled = true)))
    }
}
