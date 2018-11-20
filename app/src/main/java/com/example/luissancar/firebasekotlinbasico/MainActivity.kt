package com.example.luissancar.firebasekotlinbasico

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = FirebaseDatabase.getInstance()
        dbReference = database.getReference("Nombres")
        button.setOnClickListener {
            anadir()
        }
        textView.setMovementMethod(ScrollingMovementMethod())
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                textView.text=""
                println("data change")
                val gson= Gson()
                 for (objj  in dataSnapshot.children){
                    val registro=objj.getValue()
                     try {
                    val reg:Persona=gson.fromJson(registro.toString(),Persona::class.java)
                     textView.text=textView.text.toString() + "\n"+ajustarString(reg.nombre).toString() +"   "+reg.apellidos
                     //textView.text=textView.text.toString() + "\n"+reg.nombre +"   "+reg.apellidos
                         }
                     catch (e: com.google.gson.JsonSyntaxException)
                     {}

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        dbReference.child("dat").addValueEventListener(menuListener)

    }

    fun anadir() {

        if (editTextApellidos.length()==0 || editTextNombre.length()==0)
            return
        val nombre=Persona(editTextNombre.text.toString(),editTextApellidos.text.toString())
        dbReference.child("dat").push().setValue(nombre)

    }


    // ajusta la string pasada a 20 digitos
    fun ajustarString(s:String):String {
        if (s.length>20){
            return s.substring(0,19)
        }
        else
            if (s.length<20){
                var s2=s
                while (s2.length<20){
                    s2=s2+" "
                }
                return s2
            }
        return s
    }


}
