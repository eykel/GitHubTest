package com.s2start.githubtest.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.s2start.githubtest.R
import com.s2start.githubtest.service.model.GitUserDetail
import com.s2start.githubtest.util.Constants
import com.s2start.githubtest.view.viewmodel.GitUserViewModel
import kotlinx.android.synthetic.main.activity_git_user_detail.*
import kotlinx.android.synthetic.main.row_item.view.*
import kotlinx.android.synthetic.main.toolbar.*

class GitUserDetailActivity : AppCompatActivity() {

    private lateinit var mGitUserDetail : GitUserDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_git_user_detail)

        setupListener()
        adjustToolbar()
        loadData()

    }

    private fun adjustToolbar() {
        toolbar_title.text = getString(R.string.detail_toolbar_title)
        favorite_button.visibility = View.INVISIBLE
    }

    private fun setupListener() {
        back.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun loadData() {
        val bundle = intent.extras
        if(bundle != null){
            val gson  = Gson()
            val gitUserDetailString = bundle.getString(Constants.GitUser.GITUSER)
            mGitUserDetail = gson.fromJson(gitUserDetailString, GitUserDetail::class.java)

            Glide.with(this)
                .load(mGitUserDetail.avatar)
                .into(avatar)

            if (mGitUserDetail.nickname.isEmpty())
                nickname.text = mGitUserDetail.login
            else
                nickname.text = mGitUserDetail.nickname

            if(mGitUserDetail.email.isEmpty())
                email.text = getString(R.string.email_empty)
            else
                email.text = mGitUserDetail.email

            if(mGitUserDetail.location.isEmpty())
                location.text = getString(R.string.location_empty)
            else
                location.text = mGitUserDetail.location



            if(mGitUserDetail.bio.isEmpty())
                bio.text = getString(R.string.bio_empty)
            else
                bio.text = mGitUserDetail.bio
        }
    }

}