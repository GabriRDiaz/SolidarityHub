package com.upv.SolldarityHub.factory

class PokemonFactory {
    fun createPokemon(type: String): Pokemon {
        return when (type) {
            "Charmander" -> Charmander()
            "Bulbasaur" -> Bulbasaur()
            "Squirtle" -> Squirtle()
            else -> throw IllegalArgumentException("Unknown Pokémon type")
        }
    }
}