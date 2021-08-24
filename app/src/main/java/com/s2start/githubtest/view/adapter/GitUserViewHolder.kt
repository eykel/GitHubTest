package com.s2start.githubtest.view.adapter

import android.view.View
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.s2start.githubtest.R
import com.s2start.githubtest.service.model.GitUser
import com.s2start.githubtest.view.GitUserListener
import kotlinx.android.synthetic.main.row_item.view.*

class GitUserViewHolder (itemView: View, private val listener: GitUserListener): RecyclerView.ViewHolder(itemView){

    fun bind(gitUser: GitUser){
        var container = itemView.container
        var login = itemView.login
        var favorite = itemView.favorite

        login.text = gitUser.nickName
        if(gitUser.favorite){
            favorite.setImageDrawable(getDrawable(itemView.context, R.drawable.ic_favorite_checked))
        }else{
            favorite.setImageDrawable(getDrawable(itemView.context, R.drawable.ic_favorite_unckecked))
        }

        container.setOnClickListener{
            listener.onClick(gitUser.id)
        }

        favorite.setOnClickListener{
            listener.onFavoriteClick(gitUser.id)
        }

        Glide.with(itemView.context)
            .load(gitUser.avatar)
            .circleCrop()
            .into(itemView.avatar)
    }
}