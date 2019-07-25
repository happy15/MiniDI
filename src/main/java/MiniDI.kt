package com.company

import kotlin.reflect.KClass

/**
 * Inspired by http://www.twittee.org/ and Py's saying on that
 * the key to a dependency injection is a map from names to factories.
 */
class MiniDI {

    private val factories: MutableMap<String, (MiniDI) -> Any> = mutableMapOf()

    fun <T> inject(clazz: Class<T>): T = factories[clazz.canonicalName]?.invoke(this) as T

    fun <T> config(clazz: Class<T>, body: (MiniDI) -> Any) {
        factories[clazz.canonicalName] = body
    }
}

private fun tryMiniDI() {
    val di = MiniDI()

    di.config(Room::class.java) {
        val desk = it.inject(Desk::class.java)
        val chair = it.inject(Chair::class.java)
        Room(desk, chair)
    }
    di.config(Desk::class.java) {
        Desk()
    }
    di.config(Chair::class.java) {
        Chair()
    }

    val room = di.inject(Room::class.java)
    println(room)
}

class MiniDI2 : HashMap<Any, (MiniDI2) -> Any>() {
    operator fun <T: Any> set(clazz: KClass<T>, factory: (MiniDI2) -> Any) {
        put(clazz.java.canonicalName, factory)
    }

    operator fun <T: Any> get(clazz: KClass<T>): T {
        return get(clazz.java.canonicalName)?.invoke(this) as T
    }
}

private fun tryMiniDI2() {
    val di = MiniDI2()
    di[Room::class] = { Room(it[Desk::class], it[Chair::class]) }
    di[Desk::class] = { Desk() }
    di[Chair::class] = { Chair() }
    println(di)
    println(di[Room::class])
}

class Room(private val desk: Desk, private val chair: Chair) {
    override fun toString() = "This is a room, it has: $desk, $chair"
}

class Chair {
    override fun toString() = "a chair"
}

class Desk {
    override fun toString() = "a desk"
}

fun main() {
    tryMiniDI()
    tryMiniDI2()
}



