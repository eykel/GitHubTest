package com.s2start.githubtest.view.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence
import com.google.gson.Gson
import com.s2start.githubtest.R
import com.s2start.githubtest.service.model.ListGitUser
import com.s2start.githubtest.util.Constants
import com.s2start.githubtest.util.SecurityPreferences
import com.s2start.githubtest.util.Status
import com.s2start.githubtest.util.Util
import com.s2start.githubtest.view.viewmodel.GitHubSearchViewModel
import kotlinx.android.synthetic.main.activity_git_hub_search.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class GitHubSearchActivity : AppCompatActivity() {

    private val mViewModel by viewModel<GitHubSearchViewModel>()
    lateinit var dialog: AlertDialog
    private lateinit var mParams : String
    private val preferences : SecurityPreferences by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_git_hub_search)

        setupListener()
        setupObservers()
        setupStarters()
        startGuide()

    }

    private fun setupStarters() {
        toolbar_title.text = getString(R.string.search_toolbar_title)
    }

    private fun startGuide() {
        val firstOpen = preferences.get(Constants.APP.FIRSTOPEN, true)
        if(firstOpen){
            var first = createGuide(
                favorite_button,
                getString(R.string.guide_fav_desc),
                getString(R.string.guide_fav_title)
            )
            var second = createGuide(
                back,
                getString(R.string.guide_back_desc),
                getString(R.string.guide_back_title)
            )
            var thirth = createGuide(
                search,
                getString(R.string.guide_search_desc),
                getString(R.string.guide_search_title)
            )
            BubbleShowCaseSequence()
                .addShowCase(first)
                .addShowCase(second)
                .addShowCase(thirth)
                .show()

            preferences.store(Constants.APP.FIRSTOPEN, false)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun createGuide(view: View, desc: String, title: String): BubbleShowCaseBuilder {
        return BubbleShowCaseBuilder(this)
            .title(title)
            .backgroundColor(resources.getColor(R.color.secondaryDarkColor)) //Bubble background color
            .textColor(Color.WHITE)
            .description(desc)
            .targetView(view)
    }

    private fun setupListener() {
        search.setOnClickListener {
            showLoading(this)
            if(Util.isInternetAvailable(this)){
                val valid = validate()
                if (valid) {
                    mParams = getParams()
                    mViewModel.getListUsers(mParams)

                } else {
                    dialog.dismiss()
                }
            }else{
                dialog.dismiss()
                Toast.makeText(this, getString(R.string.search_offline_error_message), Toast.LENGTH_SHORT).show()
            }
        }


        nickname.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) nickname_container.background = null
        }

        location.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) location_container.background = null
        }

        language.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) language_container.background = null
        }

        back.setOnClickListener {
            super.onBackPressed()
        }

        favorite_button.setOnClickListener {
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validate(): Boolean {
        if (nickname.text.isEmpty() && location.text.isEmpty() && language.text.isEmpty()) {
            nickname_container.background =
                ContextCompat.getDrawable(this, R.drawable.error_background)
            location_container.background =
                ContextCompat.getDrawable(this, R.drawable.error_background)
            language_container.background =
                ContextCompat.getDrawable(this, R.drawable.error_background)
            return false
        }
        return true
    }

    private fun setupObservers() {
        mViewModel.listUser.observe(this,  {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { data -> retrieveUserList(data) }
                    }
                    Status.ERROR -> {
                        dialog.dismiss()
                        Toast.makeText(this, getString(R.string.search_error), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        })
    }

    private fun retrieveUserList(ul: ListGitUser) {
        val gson = Gson()
        val intent = Intent(this, GitUserActivity::class.java)
        val bundle = Bundle()
        bundle.putString(Constants.GitUser.LISTUSER, gson.toJson(ul))
        bundle.putString(Constants.GitUser.PARAMS, mParams)
        intent.putExtras(bundle)
        dialog.dismiss()
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        logo.animate().apply {
            duration = 1000
            scaleXBy(.200f)
            scaleYBy(.200f)
        }.withEndAction {
            logo.animate().apply {
                duration = 1000
                scaleXBy(-.200f)
                scaleYBy(-.200f)
            }
        }.start()
    }

    private fun getParams(): String {
        var parameter = ""
        if (!nickname.text.isEmpty())
            parameter += getString(R.string.parameter_name, nickname.text)
        if (!location.text.isEmpty())
            parameter += getString(R.string.parameter_location, location.text)
        if (!language.text.isEmpty())
            parameter += getString(R.string.parameter_language, language.text)

        return parameter
    }

    private fun showLoading(activity: Activity) {

        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.custom_loading, null, false)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(600, 600);
    }
}