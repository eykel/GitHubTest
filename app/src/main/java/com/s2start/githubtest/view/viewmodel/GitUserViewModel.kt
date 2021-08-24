package com.s2start.githubtest.view.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.s2start.githubtest.R
import com.s2start.githubtest.service.model.GitUser
import com.s2start.githubtest.service.model.GitUserDetail
import com.s2start.githubtest.service.model.ListGitUser
import com.s2start.githubtest.service.repository.GitUserRepository
import com.s2start.githubtest.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GitUserViewModel(private var  mGitUserRepository : GitUserRepository, val context: Context) : ViewModel() {

    private var mListUser = MutableLiveData<Resource<ListGitUser>>()
    val listUser: LiveData<Resource<ListGitUser>> = mListUser

    private var mUpdateUser = MutableLiveData<Resource<Boolean>>()
    val updateUser: LiveData<Resource<Boolean>> = mUpdateUser

    private var mUserDetail = MutableLiveData<Resource<GitUserDetail>>()
    val userDetail : LiveData<Resource<GitUserDetail>> = mUserDetail


    fun getListUsers(parameter: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val remoteList = mGitUserRepository.getGitUserList(parameter)
                mListUser.postValue(Resource.success(data = updateGitUsers(remoteList)))
            } catch (exception: Exception) {
                mListUser.postValue(Resource.error(
                    data = null,
                    message = exception.message ?:  context.getString(R.string.something_wrong_error_message)
                ))
            }
        }
    }

    fun updateGitUsers(listGitUser : ListGitUser) : ListGitUser{
        val favoriteList = mGitUserRepository.getAll()
        for (favoriteUser in favoriteList){
            val user =  listGitUser.users.find { it.id == favoriteUser.id }
            if(user != null){
                listGitUser.users.get(listGitUser.users.indexOf(user)).favorite = true
            }
        }
        return listGitUser
    }

    fun updateUser(user: GitUser){
        try {
            if(user.favorite)
                mUpdateUser.value = Resource.success(data = mGitUserRepository.save(user))
            else
                mUpdateUser.value = Resource.success(data = mGitUserRepository.delete(user))
        }catch (exception: Exception){
            mUpdateUser.value =  Resource.error(data = null,message = exception.message ?: context.getString(R.string.something_wrong_error_message))
        }
    }

    fun getUserDetail(login: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mUserDetail.postValue(Resource.success(data = mGitUserRepository.getUserDetail(login)))
            } catch (exception: Exception) {
                mUserDetail.postValue(Resource.error(
                    data = null,
                    message = exception.message ?: context.getString(R.string.something_wrong_error_message)
                ))
            }
        }
    }
}