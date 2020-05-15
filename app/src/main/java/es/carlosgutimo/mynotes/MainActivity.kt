package es.carlosgutimo.mynotes

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.note_ticket.view.*

class MainActivity : AppCompatActivity() {
    private var listOfNotes: ArrayList<Note> = ArrayList()


    private fun View.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val sv: SearchView = menu!!.findItem(R.id.bu_menu_search).actionView as SearchView
        val sm: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))


        val menuItem = menu.findItem(R.id.bu_menu_search)
        val searchView = menuItem?.actionView as SearchView




        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchView.showKeyboard()
            } else {
                searchView.hideKeyboard()
            }
        }

        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                searchView.isIconified = false
                searchView.requestFocusFromTouch()
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                // when back, clear all search
                searchView.setQuery("", true)
                return true
            }
        })

        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                loadQuery("%$newText%")
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bu_AddNote.setOnClickListener {
            val intent = Intent(this, AddNotesActivity::class.java)
            startActivity(intent)
        }

        loadQuery("%")
    }

    override fun onResume() {
        super.onResume()
        loadQuery("%")
        if (listOfNotes.isEmpty()) Toast.makeText(
            this,
            getString(R.string.add_note),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun loadQuery(title: String) {
        val dbManager = DbManager(this)
        val projections: Array<String> = arrayOf("ID", "Title", "Description")
        val selectionArgs: Array<String> = arrayOf(title)
        val cursor: Cursor = dbManager.query(projections, "Title like ?", selectionArgs, "Title")
        listOfNotes.clear()
        if (cursor.moveToFirst()) {
            do {
                val noteID: Int = cursor.getInt(cursor.getColumnIndex("ID"))
                val noteTitle: String = cursor.getString(cursor.getColumnIndex("Title"))
                val noteDescription: String = cursor.getString(cursor.getColumnIndex("Description"))
                listOfNotes.add(Note(noteID, noteTitle, noteDescription))

            } while (cursor.moveToNext())
        }
        val myNotesAdapter = MyNotesAdapter(this, listOfNotes)
        listView_Notes.adapter = myNotesAdapter
    }

    inner class MyNotesAdapter(context: Context, listOfNotesAdapter: ArrayList<Note>) :
        BaseAdapter() {
        private var lisOfNotesAdapter = listOfNotesAdapter
        private var context: Context? = context


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val myView = layoutInflater.inflate(R.layout.note_ticket, null)
            val myNote = lisOfNotesAdapter[position]
            myView.tv_TitleNotes.text = myNote.noteTitle
            myView.tv_DescriptionNotes.text = myNote.noteDescription
            myView.bu_DeleteNotes.setOnClickListener {
                val dbManager = DbManager(this.context!!)
                val selectionArgs = arrayOf(myNote.noteID.toString())
                dbManager.delete("ID=?", selectionArgs)
                loadQuery("%")
            }
            myView.bu_EditNotes.setOnClickListener {
                editNote(myNote)
            }

            return myView
        }

        override fun getItem(position: Int): Any {
            return lisOfNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return lisOfNotesAdapter.size
        }
    }

    fun editNote(note: Note) {
        val intent = Intent(this, AddNotesActivity::class.java)
        intent.putExtra("ID", note.noteID)
        intent.putExtra("Title", note.noteTitle)
        intent.putExtra("Description", note.noteDescription)
        startActivity(intent)
    }
}
