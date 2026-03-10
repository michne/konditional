@file:OptIn(io.amichne.konditional.api.KonditionalInternalApi::class)

package io.amichne.konditional.core.instance

import io.amichne.konditional.core.FlagDefinition
import io.amichne.konditional.core.features.Feature

data class Configuration(
    override val flags: Map<Feature<*, *, *>, FlagDefinition<*, *, *>>,
    override val metadata: ConfigurationMetadata = ConfigurationMetadata(),
) : ConfigurationView

data class ConfigurationMetadata(
    override val version: String? = null,
    override val generatedAtEpochMillis: Long? = null,
    override val source: String? = null,
) : ConfigurationMetadataView
