package com.clarifai.clarity
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.view.*
import java.io.File
import java.io.IOException
import android.widget.Toast
import android.os.Environment
import kotlinx.android.synthetic.main.activity_graph_view.*
import android.graphics.BitmapFactory
import android.nfc.Tag
import android.util.Log
import android.widget.Button
import org.json.*

class Graph_view : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph_view)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        val pictures = readfile()
        var index=0
        ChangeImage(pictures.getJSONObject(index).get("Filename").toString())
        textView.text = pictures.getJSONObject(index).toString().replace(",","\n")
        test_button.setOnClickListener{
            if(index<pictures.length()-1){
            index++
            }
            else{index = 0}
            ChangeImage(pictures.getJSONObject(index).get("Filename").toString())

            textView.text = pictures.getJSONObject(index).toString().replace(",","\n")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.gallery -> {
            val intent = Intent(this, Graph_view::class.java)
            startActivity(intent)
            Toast.makeText(this, "Gallery Clicked", Toast.LENGTH_SHORT).show()
            true
        }

        R.id.interval -> {
            Toast.makeText(this, "Interval Clicked", Toast.LENGTH_SHORT).show()
            true
        }

        R.id.settings -> {
            val intent = Intent(this, OptionsAction::class.java)
            startActivity(intent)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun ChangeImage(path: String){
        val assetsBitmap:Bitmap? = getBitmapFromAssets(path)
        image_view_assets.setImageBitmap(assetsBitmap)
    }

    private fun readfile(): JSONArray{
        val file = File("/storage/emulated/0/Pictures/SarApp/PredictionData.json")
        val data = File(file.absolutePath).readText()
//        var processed = data.split(Regex("""\{|\}"""))
        var processed = data.split(';')
        processed = processed.dropLast(1)
        Log.d(TAG,processed.toString())
        var pictures = JSONArray()
        for (data in processed) {
            pictures.put(JSONObject(data))
        }
        Log.d(TAG,pictures.toString())
        return pictures
    }

    private fun getBitmapFromAssets(path: String): Bitmap? {
        return try {
            //[^\\]+$ <- removes the \\'s
            var path = path.replace("\\","")
            BitmapFactory.decodeFile(path)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    companion object {
        private val TAG = Graph_view::class.java.simpleName
    }
}
