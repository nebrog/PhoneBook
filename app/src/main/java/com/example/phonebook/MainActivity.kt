package com.example.phonebook

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private val originalContacts = ArrayList<NumbersModel>()

    private val adapter by lazy { NumbersAdapter(this@MainActivity.layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val hasReadContactPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            READ_CONTACTS_GRANTED = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CODE_READ_CONTACTS
            )
        }
        if (READ_CONTACTS_GRANTED) {
            loadContacts()
        }
        val editText: EditText = findViewById(R.id.editText)
        editText.addTextChangedListener {
            findByName(it.toString())
        }

    }

    fun findByName(word: String) {
        val filteredContacts = originalContacts.filter {
            return@filter it.name.contains(word, true) || it.numbers.contains(word, true)
        }
        adapter.setChanges(filteredContacts)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                READ_CONTACTS_GRANTED = true
            }
        }
        if (READ_CONTACTS_GRANTED) {
            loadContacts()
        } else {
            Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("Range")
    private fun loadContacts() {
        val contentResolver = contentResolver
        val cursor =
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )
        if (cursor != null) {
            while (cursor.moveToNext()) {

                val name = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                )
                val number =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val contact = NumbersModel(name, number)
                originalContacts.add(contact)
            }
            cursor.close()
        }
        originalContacts.sortBy { it.name }

        adapter.setChanges(originalContacts)

        val recycler: RecyclerView = findViewById(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter


    }

    companion object {
        private const val REQUEST_CODE_READ_CONTACTS = 1
        private var READ_CONTACTS_GRANTED = false
    }
}