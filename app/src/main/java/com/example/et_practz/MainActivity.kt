package com.example.et_practz

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.example.et_practz.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    val jobTimeOut = 1900L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        binding.testNetworkBTN.setOnClickListener {
//            if (checkForInternet(this)){
//                Snackbar.make(this,view,"Connected",Snackbar.LENGTH_LONG).show()
//            }else{
//                Snackbar.make(this,view,"No Connection",Snackbar.LENGTH_LONG).show()
//            }
//        }
        binding.testNetworkBTN.setOnClickListener {
            setNewText("Click!")
            CoroutineScope(IO).launch {
                apiRequest()
            }
        }
        binding.sampleET.setOnClickListener{

        }

        binding.sampleET.setOnEditorActionListener{
            view, actionId, eventKey->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || eventKey == null
                || eventKey.keyCode == KeyEvent.KEYCODE_ENTER){
                    val msg:String = "Notified!"
                binding.textView.text = msg
            }
            false
        }
    }

    private suspend fun apiRequest(){
        withContext(IO){
            val job = withTimeoutOrNull(jobTimeOut){
                val result1 = getResultsFromApi()
                println("debug: result #1: $result1")

                val result2 = getResult2FromApi()
                setTextOnMainThread("Got $result2")
            }
            if (job == null){
                val cancelMessage = "Cancelling job took too long  that $jobTimeOut ms"
                print("debug: $cancelMessage")
                setTextOnMainThread(cancelMessage)
            }
        }
    }

    private fun setNewText(input: String) {
        val newText = binding.textView.text.toString() + "\n$input"
        binding.textView.text = newText
    }

    private suspend fun setTextOnMainThread(input: String){
        withContext(Main){
            setNewText(input)
        }
    }
    private suspend fun getResultsFromApi(): String{
        delay(1000) // Doesnt block thread. Just suspends the coroutine inside
        return "Result #1"
    }
    private suspend fun getResult2FromApi(): String{
        delay(1000)
        return "Result #2"
    }

    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}