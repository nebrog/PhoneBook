package com.example.phonebook

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private val originalContacts = ArrayList<NumbersModel>()
    private val search:ImageButton by lazy {findViewById(R.id.search_button)}
    private val cancel:ImageButton by lazy {findViewById(R.id.cancel_button)}
    private val editText: EditText by lazy {findViewById(R.id.editText)}
    private val settingButton: Button by lazy {findViewById(R.id.setting_button) }
    private val textNoPermission: TextView by lazy{findViewById(R.id.text_no_permission)}
    private val recycler: RecyclerView by lazy {findViewById(R.id.recyclerView)}

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

        editText.addTextChangedListener {
            findByName(it.toString())
        }


        search.setOnClickListener {
            search.visibility =View.INVISIBLE
            cancel.visibility = View.VISIBLE
            editText.visibility = View.VISIBLE
        }
        cancel.setOnClickListener {
            search.visibility =View.VISIBLE
            cancel.visibility = View.INVISIBLE
            editText.visibility = View.INVISIBLE
            editText.text = null
            originalContacts.clear()
            loadContacts()
        }
    }

    private fun findByName(word: String) {
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
            search.visibility = View.INVISIBLE
            cancel.visibility = View.INVISIBLE
            editText.visibility = View.INVISIBLE
            recycler.visibility = View.INVISIBLE
            textNoPermission.visibility = View.VISIBLE
            settingButton.visibility = View.VISIBLE
            settingButton.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
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


        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter


    }

    companion object {
        private const val REQUEST_CODE_READ_CONTACTS = 1
        private var READ_CONTACTS_GRANTED = false
    }
}