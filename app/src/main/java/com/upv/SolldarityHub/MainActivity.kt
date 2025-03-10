package com.upv.SolldarityHub

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.upv.SolldarityHub.factory.PokemonFactory

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private val factory = PokemonFactory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textViewPokemon)

        // Correct type for buttons (Button, not Button<GenericType>)
        val btnCharmander: Button = findViewById(R.id.btnCharmander)
        val btnBulbasaur: Button = findViewById(R.id.btnBulbasaur)
        val btnSquirtle: Button = findViewById(R.id.btnSquirtle)
        val btnRandom: Button = findViewById(R.id.btnRandom)

        // Set OnClickListeners
        btnCharmander.setOnClickListener { showPokemonAttack("Charmander") }
        btnBulbasaur.setOnClickListener { showPokemonAttack("Bulbasaur") }
        btnSquirtle.setOnClickListener { showPokemonAttack("Squirtle") }
        btnRandom.setOnClickListener {
            val randomPokemon = listOf("Charmander", "Bulbasaur", "Squirtle").random()
            showPokemonAttack(randomPokemon)
        }
    }

    private fun showPokemonAttack(pokemonType: String) {
        val pokemon = factory.createPokemon(pokemonType)
        textView.text = "${pokemon.attack()}"
    }
}
