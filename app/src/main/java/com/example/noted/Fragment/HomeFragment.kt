package com.example.noted.Fragment

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
//import com.example.noted.Adapter.NoteCatAdapter
import com.example.noted.R
import com.example.noted.RoomDB.NoteCatDatabase
import com.example.noted.RoomDB.NoteCategory
import com.example.noted.Utils.toast
import com.example.noted.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import java.io.*
import java.util.*

class HomeFragment : BaseFragment() {

    private var homeBinding: FragmentHomeBinding? = null
    lateinit var dialog: Dialog
    lateinit var img_pic: TextView
    lateinit var btn_addCat: Button
    lateinit var cat_imag: ImageView
    lateinit var edt_catTitle: EditText
    private var noteCategory: NoteCategory? = null

    var picturePath = ""
    lateinit var selectedImage: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeBinding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("TAG", "Home fragment")

        homeBinding!!.rvCatList.setHasFixedSize(true)
        homeBinding!!.rvCatList.layoutManager = GridLayoutManager(context, 2)

        loadData()
        homeBinding!!.addCat.setOnClickListener {
            openAddCatDialog()
        }
    }

    private fun loadData() {
        launch {
            context?.let {
                val notesCat = NoteCatDatabase(it).getNoteCat().getNoteCategory()
                val noteCatAdapetr = NoteCatAdapter(it, notesCat)
                homeBinding!!.rvCatList.adapter = noteCatAdapetr
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("TAG", "onResume: ")
        loadData()
    }

    private fun openAddCatDialog() {

        dialog = Dialog(this.context!!)
        dialog.getWindow()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_addcatgory)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)

        img_pic = dialog.findViewById(R.id.txt_pic) as TextView
        btn_addCat = dialog.findViewById<Button>(R.id.btn_addCat)
        val img_close = dialog.findViewById<ImageView>(R.id.img_close)
        cat_imag = dialog.findViewById<ImageView>(R.id.img_cat)
        edt_catTitle = dialog.findViewById<EditText>(R.id.edt_catTitle)

        img_close.setOnClickListener { dialog.dismiss() }
        img_pic.setOnClickListener {
            if (checkAndRequestPermissions())
                selectImage()
        }

        btn_addCat.setOnClickListener {

            val cat_name = edt_catTitle.text.toString().trim()
            if (cat_name.isEmpty()) {
                edt_catTitle.error = "plase Enter name"
                edt_catTitle.requestFocus()
            } else {

                launch {
                    context?.let {
                        val noteCategory = NoteCategory()
                        noteCategory.cat_title = cat_name
                        noteCategory.cat_image = picturePath
                        NoteCatDatabase(it).getNoteCat().addNoteCategory(noteCategory)
                        it.toast("Note Category Add")
                        dialog.dismiss()
                    }
                }
            }
        }
        dialog.show()
//        val window: Window = dialog.getWindow()!!
//        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun selectImage() {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your profile picture")

        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                val takePicture =
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 0)
            } else if (options[item] == "Choose from Gallery") {
                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, 1)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == RESULT_OK && data != null) {
//                    selectedImage = data.extras!!["data"] as Bitmap
//                    cat_imag.setImageBitmap(selectedImage)
//                    picturePath = storeImage(selectedImage).toString()
//                    Log.e("TAG", "capture img " + picturePath)

                    selectedImage = data.extras!!["data"] as Bitmap
                    cat_imag.setImageBitmap(selectedImage)

                    val tempUri = getImageUri(this.context!!, selectedImage)
                    val finalFile = File(getRealPathFromURI(tempUri))
                    val uri = Uri.fromFile(finalFile)
                    picturePath = uri.path!!
                    Log.e("TAG", "capture img " + picturePath)
                    /*   picturePath = data.data.toString()
                       cat_imag.setImageBitmap(BitmapFactory.decodeFile(picturePath))
                       Log.e("TAG", "capture img " + picturePath)*/

                }
                1 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage: Uri? = data.data
                    val filePathColumn =
                        arrayOf(MediaStore.Images.Media.DATA)
                    if (selectedImage != null) {
                        val cursor: Cursor? = context!!.contentResolver.query(
                            selectedImage,
                            filePathColumn, null, null, null
                        )
                        if (cursor != null) {
                            cursor.moveToFirst()
                            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                            picturePath = cursor.getString(columnIndex)
                            cat_imag.setImageBitmap(BitmapFactory.decodeFile(picturePath))
                            Log.e("TAG", "PicturePath" + picturePath)
                            cursor.close()
                        }
                    }
                }
            }
        }
    }

    fun getRealPathFromURI(uri: Uri?): String? {
        var path = ""
        if (context!!.contentResolver != null) {
            val cursor: Cursor =
                context!!.contentResolver.query(uri!!, null, null, null, null)!!
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(
                inContext.getContentResolver(),
                inImage,
                "Title",
                null
            )
        return Uri.parse(path)
    }

    private fun checkAndRequestPermissions(): Boolean {
        val camera = context?.let { checkSelfPermission(it, "android.permission.CAMERA") }
        val writeStorage =
            context?.let { checkSelfPermission(it, "android.permission.WRITE_EXTERNAL_STORAGE") }
        val readStorage =
            context?.let { checkSelfPermission(it, "android.permission.READ_EXTERNAL_STORAGE") }
        val listPermissionNeeded: MutableList<String> =
            ArrayList()
        if (writeStorage != 0) {
            listPermissionNeeded.add("android.permission.WRITE_EXTERNAL_STORAGE")
        }
        if (readStorage != 0) {
            listPermissionNeeded.add("android.permission.READ_EXTERNAL_STORAGE")
        }
        if (camera != 0) {
            listPermissionNeeded.add("android.permission.CAMERA")
        }
        if (listPermissionNeeded.isEmpty()) {
            return true
        }
        ActivityCompat.requestPermissions(
            context as Activity,
            listPermissionNeeded.toTypedArray(),
            100
        )
        return false
    }

    inner class NoteCatAdapter(
        private val context: Context,
        private val noteCatList: List<NoteCategory>
    ) :
        RecyclerView.Adapter<NoteCatAdapter.ViewHolder>() {

        lateinit var dialog: Dialog
        lateinit var img_pic: TextView
        lateinit var btn_addCat: Button
        lateinit var cat_imag: ImageView
        lateinit var edt_catTitle: EditText
        lateinit var noteCategory: NoteCategory

        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
            val v =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_notecategory, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return noteCatList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.txt_catname.text = noteCatList[position].cat_title

            Glide.with(context)
                .load(noteCatList[position].cat_image)
                .into(holder.img_cat)

            holder.img_more.setOnClickListener {
                val popupMenu = PopupMenu(context, holder.img_more)
                popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {

                        R.id.action_edit -> editNoteCat(position)

                        R.id.action_delete -> {

                            deleteNoteCat(position)
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }

        private fun deleteNoteCat(position: Int) {

            Thread(Runnable {
                //val noteCategory = NoteCategory(noteCatList[position])
                NoteCatDatabase(context).getNoteCat().deleteNoteCat(noteCatList[position])
                noteCatList.drop(position)
                notifyItemRemoved(position)
                notifyItemRangeRemoved(position, noteCatList.size)
            }).start()
        }


        private fun editNoteCat(position: Int) {
            dialog = Dialog(context)
            dialog.getWindow()
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_addcatgory)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)

            img_pic = dialog.findViewById(R.id.txt_pic) as TextView
            btn_addCat = dialog.findViewById<Button>(R.id.btn_addCat)
            val img_close = dialog.findViewById<ImageView>(R.id.img_close)
            cat_imag = dialog.findViewById<ImageView>(R.id.img_cat)
            edt_catTitle = dialog.findViewById<EditText>(R.id.edt_catTitle)

            img_close.setOnClickListener { dialog.dismiss() }
            img_pic.setOnClickListener {

                selectImage()
            }

            noteCategory = NoteCategory()
            edt_catTitle.setText(noteCategory.cat_title)
            Glide.with(context)
                .load(noteCategory.cat_image)
                .into(cat_imag)

            btn_addCat.setOnClickListener {

                val cat_name = edt_catTitle.text.toString().trim()
                if (cat_name.isEmpty()) {
                    edt_catTitle.error = "plase Enter name"
                    edt_catTitle.requestFocus()
                } else {
//                    val noteCategory = NoteCategory(cat_name, picturePath)
//                    NoteCatDatabase(context).getNoteCat().addNoteCategory(noteCategory)
//                    dialog.dismiss()
                }
            }
            dialog.show()
        }


        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val txt_catname = view.findViewById<TextView>(R.id.txt_catname)
            val img_cat = view.findViewById<ImageView>(R.id.img_cat)
            val img_more = view.findViewById<ImageView>(R.id.img_more)
        }

    }
}