package com.teamproject.wounddetection.viewmodel

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamproject.wounddetection.data.api.PatientApi
import com.teamproject.wounddetection.data.model.Case
import com.teamproject.wounddetection.data.model.CasePost
import com.teamproject.wounddetection.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CaseSelectionPopupViewModel(
    private val api: PatientApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _cases: MutableLiveData<Resource<List<Case>>> =
        MutableLiveData<Resource<List<Case>>>()
    val cases: LiveData<Resource<List<Case>>> = _cases

    private val _uploadingPhoto: MutableLiveData<Resource<Unit>> = MutableLiveData<Resource<Unit>>()
    val uploadingPhoto: LiveData<Resource<Unit>> = _uploadingPhoto

    companion object {
        private const val TAG = "CaseSelectionPopupViewModel"
    }

    fun getCases(patientCode: Int) {
        viewModelScope.launch(dispatcher) {
            try {
                _cases.postValue(Resource.loading(_cases.value?.data ?: listOf()))
                val patient = api.getPatient(patientCode.toString())
                _cases.postValue(Resource.success(patient.cases))
            } catch (e: Throwable) {
                _cases.postValue(
                    Resource.error(
                        "An error occurred white getting case lis",
                        _cases.value?.data ?: listOf()
                    )
                )
                Log.d(TAG, "getCases: ${e.message}")
            }
        }
    }

    fun createCase(patientCode: Int, uri: Uri) {
        viewModelScope.launch(dispatcher) {
            try {
                _uploadingPhoto.postValue(Resource.loading(Unit))
                val profile = api.getProfile()
                val date = SimpleDateFormat(
                    "yyyy-mm-dd",
                    Locale.getDefault()
                ).format(Calendar.getInstance().time)
                val newCase =
                    api.addCase(CasePost(doctor = profile.id, date = date, patient = patientCode))
                uploadPhoto(newCase.id, uri)
                _uploadingPhoto.postValue(Resource.success(Unit))
            } catch (e: Throwable) {
                Log.d(TAG, "createCase: " + e.message)
                _uploadingPhoto.postValue(
                    Resource.error(
                        "An error occurred while uploading photo",
                        null
                    )
                )
            }
        }
    }

    fun uploadToCase(caseId: Int, uri: Uri) {
        viewModelScope.launch(dispatcher) {
            try {
                _uploadingPhoto.postValue(Resource.loading(Unit))
                uploadPhoto(caseId, uri)
                _uploadingPhoto.postValue(Resource.success(Unit))
            } catch (e: Throwable) {
                Log.d(TAG, "createCase: " + e.message)
                _uploadingPhoto.postValue(
                    Resource.error(
                        "An error occurred while uploading photo",
                        null
                    )
                )
            }
        }
    }


    private suspend fun uploadPhoto(caseId: Int, uri: Uri) {
        val date = SimpleDateFormat(
            "yyyy-mm-dd",
            Locale.getDefault()
        ).format(Calendar.getInstance().time)
        val file = uri.toFile()
        val requestFile = file.asRequestBody(MultipartBody.FORM)
        val img: MultipartBody.Part =
            MultipartBody.Part.createFormData("image", file.getName(), requestFile)
        val caseIdBody = caseId.toString().toRequestBody(MultipartBody.FORM)
        val uploadDate = date.toRequestBody(MultipartBody.FORM)
        val empty = "".toRequestBody(MultipartBody.FORM)
        api.uploadWound(
            case = caseIdBody,
            uploadDate = uploadDate,
            img = img,
            depth = empty,
            category = empty,
            type = empty,
            area = empty,
            diameter = empty,
            additional = empty
        )
    }

    fun isEmpty(): Boolean = _cases.value?.data?.isEmpty() ?: true
}