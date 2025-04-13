package com.surendramaran.yolov9tflite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PostAdapter(
    private var posts: MutableList<post>,
    private val onItemClick: (post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagePost: ImageView = itemView.findViewById(R.id.img_item)
        val textTitle: TextView = itemView.findViewById(R.id.tv_item_title)
        val textDate: TextView = itemView.findViewById(R.id.tv_item_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_load_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.textTitle.text = post.title
        holder.textDate.text = "Ngày đăng: ${post.date}"

        Glide.with(holder.itemView.context)
            .load(post.imageUrl)
            .placeholder(R.drawable.alticini)
            .into(holder.imagePost)

        holder.itemView.setOnClickListener {
            onItemClick(post)
        }
    }

    override fun getItemCount(): Int = posts.size

    fun updateData(newPosts: List<post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }
}


