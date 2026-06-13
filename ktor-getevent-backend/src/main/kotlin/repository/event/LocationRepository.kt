package repository.event

import datastore.MemoryStore
import model.Location

class LocationRepository(private val store: MemoryStore) {

    fun findAll(): List<Location> = store.locations.toList()

    fun findById(id: Long): Location? = store.locations.find { it.id == id }

    fun save(location: Location): Location = store.mutate {
        val index = locations.indexOfFirst { it.id == location.id }
        if (index >= 0) locations[index] = location else locations.add(location)
        location
    }

    fun delete(id: Long): Boolean = store.mutate {
        locations.removeIf { it.id == id }
    }
}
