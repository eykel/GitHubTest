package com.s2start.githubtest.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.s2start.githubtest.R
import com.s2start.githubtest.service.model.GitUser
import com.s2start.githubtest.service.model.GitUserDetail
import com.s2start.githubtest.util.Constants
import com.s2start.githubtest.util.Status
import com.s2start.githubtest.util.Util
import com.s2start.githubtest.view.GitUserListener
import com.s2start.githubtest.view.adapter.FavoriteAdapter
import com.s2start.githubtest.view.viewmodel.FavoriteViewModel
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteActivity : AppCompatActivity() {

    private val adapter: FavoriteAdapter = FavoriteAdapter()
    private lateinit var mListener: GitUserListener
    private lateinit var mGitUserLists: MutableList<GitUser>
    private val mViewModel by viewModel<FavoriteViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        setupToolbar()
        setupListener()
        setupUI()
        setupObservers()
        loadData()
    }

    private fun setupToolbar() {
        toolbar_title.text = getString(R.string.favorite_toolbar_title)
        favorite_button.visibility = View.INVISIBLE
    }

    private fun setupListener() {
        back.setOnClickListener {
            super.onBackPressed()
        }

        mListener = object : GitUserListener {
            override fun onClick(id: Int) {
                if(Util.isInternetAvailable(applicationContext)){
                    val gitUser = mGitUserLists.find { it.id == id }
                    gitUser?.nickName?.let { mViewModel.getUserDetail(it) }
                }else{
                    Toast.makeText(applicationContext, getString(R.string.detail_user_offline_error_message), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFavoriteClick(id: Int) {
                val gitUser = mGitUserLists.find { it.id == id }
                if (gitUser?.favorite == true)
                    gitUser.favorite = false
                else
                    gitUser?.favorite = true

                mViewModel.updateUser(gitUser!!)
            }
        }
    }

    private fun setupObservers() {
        mViewModel.updateUser.observe(this, {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { isSuccess ->
                            if (isSuccess) {
                                mViewModel.getFavoriteList()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.favorite_action_error_message),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        mViewModel.userDetail.observe(this, {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { data -> retrieveGitUserDetail(data) }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        mViewModel.favoriteList.observe(this, {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { data -> retrieveFavoriteList(data) }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun loadData() {
        mViewModel.getFavoriteList()
    }

    private fun retrieveFavoriteList(fl: MutableList<GitUser>) {
        mGitUserLists = fl
        if (fl.isNotEmpty()) {
            adapter.apply {
                updateFavoriteList(fl)
            }
        } else {
            users_not_found.visibility = View.VISIBLE
        }
    }

    private fun retrieveGitUserDetail(gud: GitUserDetail) {
        val gson  = Gson()
        val intent = Intent(this, GitUserDetailActivity::class.java)
        val bundle = Bundle()
        bundle.putString(Constants.GitUser.GITUSER, gson.toJson(gud))
        intent.putExtras(bundle)
        startActivity(intent)

    }

    private fun setupUI() {
        adapter.attachListener(mListener)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}