package io.amichne.konditional.fixtures

import io.amichne.konditional.core.Namespace
import io.amichne.konditional.core.registry.InMemoryNamespaceRegistry
import io.amichne.konditional.core.registry.NamespaceRegistry
import io.amichne.konditional.values.NamespaceId
import java.util.UUID

abstract class TestNamespaceFacade(
    id: String,
    registry: NamespaceRegistry = InMemoryNamespaceRegistry(namespaceId = id),
    identifierSeed: String = UUID.randomUUID().toString(),
) : Namespace(
        id = NamespaceId(id),
        registry = registry,
        identifierSeed = NamespaceId.Seed(identifierSeed),
    ) {
    constructor(id: NamespaceId) : this(id = id.value)
}
