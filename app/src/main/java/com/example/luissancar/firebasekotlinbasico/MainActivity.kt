package com.example.luissancar.firebasekotlinbasico

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.example.luissancar.firebasekotlinbasico.R.id.button
import com.example.luissancar.firebasekotlinbasico.R.id.textView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    var listaKeys= mutableListOf<String>()  // creamos mutable list para guardar las keys para su posterior borrado


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = FirebaseDatabase.getInstance()
        dbReference = database.getReference("Nombres2")
        button.setOnClickListener {
            anadir()
        }
        buttonBorrar.setOnClickListener {
            borrar()
        }
        textView.setMovementMethod(ScrollingMovementMethod())
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                textView.text=""
                println("data change")
                listaKeys.clear()



                val gson= Gson()
                 for (objj  in dataSnapshot.children){
                    val registro=objj.getValue()


                     listaKeys.add(objj.key!!) // añadimos key a la lista

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
      //  val listaFinal= arrayOf (listaKeys.get(0),listaKeys.get(1))


       val spinnerAdap = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaKeys)

        spinner.adapter=spinnerAdap

        Log.d("SPINNER",listaKeys.toString())


        //item selected listener for spinner
        spinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("SPINNER",listaKeys.toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
               // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

    }




    fun borrar(){   // borra el primer registro
        Log.d("SPINNER",listaKeys.get(0).toString())
        Log.d("SPINNER","ddd")
        dbReference.child("dat").child(listaKeys.get(0).toString()).removeValue()

    }





    fun anadir() {

        if (editTextApellidos.length()==0 || editTextNombre.length()==0)
            return
        progressBar.visibility=View.VISIBLE
        val nombre=Persona(editTextNombre.text.toString(),editTextApellidos.text.toString())
        dbReference.child("dat").push().setValue(nombre)
        progressBar.visibility=View.GONE

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
