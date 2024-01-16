package com.sparta.seeseecallcall

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sparta.seeseecallcall.data.Contact
import com.sparta.seeseecallcall.databinding.RecyclerViewItemBinding
import org.w3c.dom.Text


class MyAdapter(private val dataset: MutableList<Contact>) :
    RecyclerView.Adapter<MyAdapter.MyHolder>() {
    interface ItemClick {
        fun onClick(view: View, position: Int)
        fun onStarClick(view:View, position:Int)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding = RecyclerViewItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClick?.onClick(holder.itemView, holder.adapterPosition)
        }
        holder.starImageView.setOnClickListener {
            itemClick?.onStarClick(holder.itemView, holder.adapterPosition)
        }

        bind(holder, position)
    }

    inner class MyHolder(private val binding: RecyclerViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val starImageView: ImageView = binding.imgStar
        val profileImageView: ImageView = binding.imgProfile
        val nameTextView: TextView = binding.tvName
        val mbtiTextView:TextView = binding.tvMbti
        val phoneNumberTextView: TextView = binding.tvPhoneNumber
    }

    private fun bind(holder:MyHolder, position: Int){
        val contact: Contact = dataset[position]

        holder.run {
            starImageView.setImageResource(
                if (contact.favorite) R.drawable.icon_star_yellow
                else R.drawable.icon_star_gray
            )
            profileImageView.run {
                if (contact.profileImage == null) {
                    setImageResource(R.drawable.profile_default)
                } else {
                    setImageURI(contact.profileImage)
                    clipToOutline = true
                }
            }
            nameTextView.text = contact.name
            mbtiTextView.text = contact.mbti
            phoneNumberTextView.text = contact.phoneNumber
        }
    }
}