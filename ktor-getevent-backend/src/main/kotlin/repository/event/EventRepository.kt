package repository.event

import datastore.MemoryStore
import model.Event

class EventRepository(private val store: MemoryStore) {

    fun findAll(): List<Event> = store.events.toList()

    fun findById(id: Long): Event? = store.events.find { it.idEvenement == id }

    fun findByLocation(locationId: Long): List<Event> =
        store.events.filter { it.lieuId == locationId }

    fun save(event: Event): Event = store.mutate {
        val index = events.indexOfFirst { it.idEvenement == event.idEvenement }
        if (index >= 0) events[index] = event else events.add(event)
        event
    }

    fun delete(id: Long): Boolean = store.mutate {
        events.removeIf { it.idEvenement == id }
    }
}
