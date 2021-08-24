package com.s2start.githubtest.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.s2start.githubtest.R
import com.s2start.githubtest.service.model.GitUser
import com.s2start.githubtest.view.GitUserListener

class GitUserAdapter : RecyclerView.Adapter<GitUserViewHolder>() {

    private var mGitUserLists : List<GitUser> = arrayListOf()
    private lateinit var mListener: GitUserListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GitUserViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent,false)
        return GitUserViewHolder(item, mListener)
    }

    override fun onBindViewHolder(holder: GitUserViewHolder, position: Int) {
        holder.bind(mGitUserLists[position])
    }

    override fun getItemCount(): Int {
        return mGitUserLists.count()
    }

    fun attachListener(listener: GitUserListener){
        mListener = listener
    }

    fun updateGitUserList(list: List<GitUser>){
        mGitUserLists = list
        notifyDataSetChanged()
    }
}