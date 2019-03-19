package com.example.nuber

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase



class MainActivity : AppCompatActivity() {
    lateinit var _db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _db = FirebaseDatabase.getInstance().getReference("salands")

        supportActionBar?.title = "Select User"


    }

    fun go(v :View){


        val k = _db.push().key
        val s = Salad("Gherkin", "Fresh and delicious", k.toString())
        _db.child(k!!).setValue(s).addOnCompleteListener {
            Toast.makeText(this, "done", Toast.LENGTH_LONG).show()
        }


    }
}

fun loadDatabase(firebaseData: DatabaseReference) {
    val availableSalads: List<Salad> = mutableListOf(
        Salad("Gherkin", "Fresh and delicious"),
        Salad("Lettuce", "Easy to prepare"),
        Salad("Tomato", "Boring but healthy"),
        Salad("Zucchini", "Healthy and gross")
    )
    availableSalads.forEach {
        val key = firebaseData.child("salads").push().key
        it.uuid = key!!
        firebaseData.child("salads").child(key).setValue(it)
    }
}

data class Salad(
    val name: String = "",
    val description: String = "",
    var uuid: String = "")