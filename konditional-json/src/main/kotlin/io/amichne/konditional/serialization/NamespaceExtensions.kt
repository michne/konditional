package io.amichne.konditional.serialization

import io.amichne.konditional.core.Namespace
import io.amichne.konditional.core.instance.Configuration
import io.amichne.konditional.serialization.snapshot.ConfigurationCodec
import io.amichne.konditional.serialization.snapshot.NamespaceSnapshotLoader

fun Namespace.toJson(): String = ConfigurationCodec.encode(this)

fun Namespace.fromJson(json: String): Result<Configuration> =
    NamespaceSnapshotLoader.forNamespace(this).load(json)
