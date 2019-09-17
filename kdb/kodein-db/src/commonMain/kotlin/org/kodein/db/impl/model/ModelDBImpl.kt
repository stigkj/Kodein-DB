package org.kodein.db.impl.model

import org.kodein.db.Options
import org.kodein.db.TypeTable
import org.kodein.db.data.DataDB
import org.kodein.db.DBListener
import org.kodein.db.model.*
import org.kodein.db.model.orm.HasMetadata
import org.kodein.db.model.orm.MetadataExtractor
import org.kodein.db.model.orm.Serializer
import org.kodein.memory.Closeable
import org.kodein.memory.util.forEachResilient

internal class ModelDBImpl(val serializer: Serializer<Any>, private val metadataExtractor: MetadataExtractor, val typeTable: TypeTable, override val data: DataDB) : ModelDB, ModelReadModule, ModelWriteModule, Closeable by data {

    internal val listeners = LinkedHashSet<DBListener<Any>>()

    override fun didAction(action: DBListener<Any>.() -> Unit) = listeners.toList().forEachResilient(action)

    internal fun getMetadata(model: Any, options: Array<out Options.Write>) =
            (model as? HasMetadata)?.getMetadata(this, *options) ?: metadataExtractor.extractMetadata(model, *options)

    override val mdb: ModelDBImpl get() = this

    override fun newBatch(): ModelBatch = ModelBatchImpl(this, data.newBatch())

    override fun newSnapshot(vararg options: Options.Read): ModelSnapshot = ModelSnapshotImpl(this, data.newSnapshot())

    override fun register(listener: DBListener<Any>): Closeable {
        val subscription = Closeable { listeners -= listener }
        if (listeners.add(listener))  listener.setSubscription(subscription)
        return subscription
    }

}
