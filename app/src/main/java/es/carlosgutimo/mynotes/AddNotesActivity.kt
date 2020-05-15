package es.carlosgutimo.mynotes

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_notes.*
import java.lang.Exception

class AddNotesActivity : AppCompatActivity() {
    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)

        try {
            val bundle: Bundle? = intent.extras
            id = bundle!!.getInt("ID", 0)
            if (id != 0) {
                et_Title.setText(bundle.getString("Title"))
                et_Description.setText(bundle.getString("Description"))
            }
        } catch (e: Exception) {
        }

        bu_AddNewNote.setOnClickListener {
            if (et_Title.text.toString() == "" || et_Description.text.toString() == "") {
                Toast.makeText(this, getString(R.string.add_title_description), Toast.LENGTH_SHORT)
                    .show()
            } else {
                val dbManager = DbManager(this)
                val values = ContentValues()

                values.put("Title", et_Title.text.toString())
                values.put("Description", et_Description.text.toString())
                if (id == 0) {
                    val id = dbManager.insert(values)
                    if (id > 0) {
                        Toast.makeText(this, getString(R.string.note_added), Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    } else
                        Toast.makeText(
                            this,
                            getString(R.string.note_error_adding),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                } else {
                    val selectionArgs: Array<String> = arrayOf(id.toString())
                    val id = dbManager.update(values, "ID=?", selectionArgs)
                    if (id > 0) {
                        Toast.makeText(this, getString(R.string.note_added), Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    } else
                        Toast.makeText(
                            this,
                            getString(R.string.note_error_adding),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                }
            }
        }
    }
}
