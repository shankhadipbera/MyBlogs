package com.shankha.myblogs.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shankha.myblogs.databinding.ActivityArticalsBinding
import com.shankha.myblogs.databinding.ArticlasItemBinding
import com.shankha.myblogs.model.BlogItemModel

class ArticalAdapter(private val context:Context, private var blogList: List<BlogItemModel>, private val itemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<ArticalAdapter.BlogViewHolder>(){

        interface OnItemClickListener{
            fun onReadMoreClick(blogItem: BlogItemModel)
            fun onEditClick(blogItem: BlogItemModel)
            fun onDeleteClick(blogItem: BlogItemModel)
        }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticalAdapter.BlogViewHolder {
       val inflater =LayoutInflater.from(parent.context)
        val binding = ArticlasItemBinding.inflate(inflater,parent,false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticalAdapter.BlogViewHolder, position: Int) {
        val blogItem= blogList[position]
        holder.bind(blogItem)
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    fun setData(blogList: ArrayList<BlogItemModel>) {
            this.blogList=blogList
        notifyDataSetChanged()
    }


    inner class BlogViewHolder (private  val binding: ArticlasItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(blogItem: BlogItemModel) {
            binding.blogHeading.text = blogItem.heading
            binding.blogText.text = blogItem.blogText
            binding.authorName.text = blogItem.userName
            binding.date.text = blogItem.datePublish
            Glide.with(binding.userPic.context).load(blogItem.imageUrl).into(binding.userPic)

            binding.btnReadM.setOnClickListener {
                itemClickListener.onReadMoreClick(blogItem)
            }
            binding.btnedit.setOnClickListener {
                itemClickListener.onEditClick(blogItem)
            }
            binding.btndelete.setOnClickListener {
                itemClickListener.onDeleteClick(blogItem)
            }

        }

    }

}