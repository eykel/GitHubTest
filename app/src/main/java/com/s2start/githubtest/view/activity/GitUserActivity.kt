package com.s2start.githubtest.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.s2start.githubtest.R
import com.s2start.githubtest.service.model.GitUser
import com.s2start.githubtest.service.model.GitUserDetail
import com.s2start.githubtest.service.model.ListGitUser
import com.s2start.githubtest.util.Constants
import com.s2start.githubtest.util.Status
import com.s2start.githubtest.util.Util
import com.s2start.githubtest.view.GitUserListener
import com.s2start.githubtest.view.adapter.GitUserAdapter
import com.s2start.githubtest.view.viewmodel.GitUserViewModel
import kotlinx.android.synthetic.main.activity_git_users.*
import kotlinx.android.synthetic.main.row_item.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class GitUserActivity : AppCompatActivity() {

    private val mViewModel by viewModel<GitUserViewModel>()
    private val adapter: GitUserAdapter = GitUserAdapter()
    private lateinit var mListener: GitUserListener
    private lateinit var mGitUserLists: MutableList<GitUser>
    private lateinit var mListGitUser: ListGitUser
    private lateinit var mParams: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_git_users)

        setupListener()
        setupUI()
        setupObservers()
        loadData()

        toolbar_title.text = getString(R.string.result_toolbar_title)

    }

    private fun setupListener() {
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

        swipeRefresh.setOnRefreshListener {
            mViewModel.getListUsers(mParams)
        }

        back.setOnClickListener {
            super.onBackPressed()
        }

        favorite_button.setOnClickListener {
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupUI() {
        adapter.attachListener(mListener)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        mViewModel.updateUser.observe(this, Observer {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { isSuccess ->
                            if (isSuccess) {
                                adapter.apply {
                                    notifyDataSetChanged()
                                }
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

        mViewModel.userDetail.observe(this, Observer {
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

        mViewModel.listUser.observe(this, Observer {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { data -> retrieveUserList(data) }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, getString(R.string.search_error), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        })
    }

    private fun retrieveUserList(ul: ListGitUser) {
        mGitUserLists = ul.users
        adapter.apply {
            updateGitUserList(mGitUserLists)
            notifyDataSetChanged()
        }
        swipeRefresh.isRefreshing = false
    }

    private fun loadData() {
        val bundle = intent.extras
        if (bundle != null) {
            val gson = Gson()
            val listUserString = bundle.getString(Constants.GitUser.LISTUSER)
            mParams = bundle.getString(Constants.GitUser.PARAMS)!!
            mListGitUser = gson.fromJson(listUserString, ListGitUser::class.java)
            mGitUserLists = mListGitUser.users
            if (mListGitUser.users.isNotEmpty()) {
                adapter.apply {
                    updateGitUserList(mListGitUser.users)
                }
                swipeRefresh.isRefreshing = false
            } else {
                users_not_found.visibility = View.VISIBLE
            }
        } else {
            Toast.makeText(this, "Lista Vazia ", Toast.LENGTH_LONG).show()
        }
    }

    private fun retrieveGitUserDetail(gud: GitUserDetail) {
        val gson = Gson()
        val intent = Intent(this, GitUserDetailActivity::class.java)
        val bundle = Bundle()
        bundle.putString(Constants.GitUser.GITUSER, gson.toJson(gud))
        intent.putExtras(bundle)
        startActivity(intent)

    }

    override fun onResume() {
        super.onResume()
        mViewModel.getListUsers(mParams)
    }


}