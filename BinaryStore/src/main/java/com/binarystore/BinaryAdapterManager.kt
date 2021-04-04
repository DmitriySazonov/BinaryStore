package com.binarystore

import com.binarystore.adapter.AdapterFactory
import com.binarystore.adapter.AdapterFactoryRegister
import com.binarystore.adapter.BinaryAdapter
import com.binarystore.adapter.BinaryAdapterProvider
import com.binarystore.meta.MetadataStore

typealias GeneratedFactory<T> = (BinaryAdapterProvider, MetadataStore) -> BinaryAdapter<T>

class BinaryAdapterManager(
        metadataStore: MetadataStore
) : BinaryAdapterProvider, AdapterFactoryRegister {

    private val adapterFactoryMap = HashMap<Class<*>, AdapterFactory<*>>()
    private val adapterMap = HashMap<Class<*>, BinaryAdapter<*>>()

    private val factoryContext = AdapterFactory.Context(this, metadataStore)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getAdapter(clazz: Class<T>): BinaryAdapter<T> {
        return adapterMap.getOrPut(clazz) {
            adapterFactoryMap[clazz]!!.create(factoryContext) as BinaryAdapter<T>
        } as BinaryAdapter<T>
    }

    override fun getAdapter(id: Int): BinaryAdapter<*> {
        TODO("Not yet implemented")
    }

    override fun <T> register(clazz: Class<T>, factory: AdapterFactory<T>) {
        adapterFactoryMap[clazz] = factory
    }
}

inline fun <T> BinaryAdapterManager.register(
        clazz: Class<T>,
        crossinline factory: () -> BinaryAdapter<T>
) {
    register(clazz) { factory() }
}

inline fun <T> BinaryAdapterManager.register(
        clazz: Class<T>,
        crossinline factory: GeneratedFactory<T>
) {
    register(clazz) { factory(it.provider, it.metadataStore) }
}