/*
package com.example.noted.Adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.noted.R
import com.example.noted.RoomDB.NoteCatDatabase
import com.example.noted.RoomDB.NoteCategory
import com.example.noted.Utils.toast
import kotlinx.coroutines.launch

class NoteCatAdapter(private val context: Context, private val noteCatList: List<NoteCategory>) :
    RecyclerView.Adapter<NoteCatAdapter.ViewHolder>() {

    lateinit var dialog: Dialog
    lateinit var img_pic: TextView
    lateinit var btn_addCat: Button
    lateinit var cat_imag: ImageView
    lateinit var edt_catTitle: EditText
    private var noteCategory: NoteCategory? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.row_notecategory, parent, false)
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

                    R.id.action_edit -> editNoteCat()

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


    private fun editNoteCat() {
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
                        val noteCategory = NoteCategory(cat_name, picturePath)
                        NoteCatDatabase(it).getNoteCat().addNoteCategory(noteCategory)
                        it.toast("Note Category Add")
                        dialog.dismiss()
                    }
                }
            }
        }
        dialog.show()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txt_catname = view.findViewById<TextView>(R.id.txt_catname)
        val img_cat = view.findViewById<ImageView>(R.id.img_cat)
        val img_more = view.findViewById<ImageView>(R.id.img_more)
    }

}*/
