package com.teamproject.wounddetection.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamproject.wounddetection.data.api.PatientApi
import com.teamproject.wounddetection.data.model.Case
import com.teamproject.wounddetection.data.model.Doctor
import com.teamproject.wounddetection.data.model.Patient
import com.teamproject.wounddetection.data.model.WoundReport
import com.teamproject.wounddetection.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PatientDataViewModel(
    private val api: PatientApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    companion object {
        private const val TAG = "PatientDataViewModel"
    }

    private var _patient = MutableLiveData<Resource<Patient>?>()
    val patient: LiveData<Resource<Patient>?> = _patient

    fun checkPatient(code: String) {
        viewModelScope.launch(dispatcher) {
            try {
                _patient.postValue(Resource.loading(null))
//                val patient = api.getPatient(code)
                val patient = Patient(
                    id = 1, name = "Name", email = "qwe@zxc.ru",
                    listOf(
                        Case(
                            1,
                            Doctor(1, "doctor@email.ru", "Doctor name 1", "Surgeon"),
                            listOf(
                                WoundReport(
                                    1,
                                    "deep",
                                    "class1",
                                    "type1",
                                    11.0,
                                    11.0,
                                    11.0,
                                    "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse1.mm.bing.net%2Fth%3Fid%3DOIP.X3mfeI7lW-x1NvHx8AZwAAHaHa%26pid%3DApi&f=1&ipt=0833a27a63ba4366a527918ae257dda887c9f96bb956a09cca056a979860368f&ipo=images"
                                ),
                                WoundReport(
                                    2,
                                    "not deep",
                                    "class2",
                                    "type2",
                                    12.0,
                                    12.0,
                                    12.0,
                                    "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.petbarn.com.au%2Fpetspot%2Fapp%2Fuploads%2F2019%2F01%2Fkitten-000017380158_Smaller.jpg&f=1&nofb=1&ipt=ed37c9a91356b71a8866925647ee12920ed16f623499c6597c8f7f42eb3529c9&ipo=images"
                                ),
                            ),
                            "11-01-2024"
                        ),
                        Case(
                            2,
                            Doctor(2, "doctor2@email.ru", "Doctor name 2", "Surgeon"),
                            listOf(
                                WoundReport(
                                    1,
                                    "deep",
                                    "class1",
                                    "type1",
                                    11.0,
                                    11.0,
                                    11.0,
                                    "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse1.mm.bing.net%2Fth%3Fid%3DOIP.X3mfeI7lW-x1NvHx8AZwAAHaHa%26pid%3DApi&f=1&ipt=0833a27a63ba4366a527918ae257dda887c9f96bb956a09cca056a979860368f&ipo=images"
                                ),
                                WoundReport(
                                    2,
                                    "not deep",
                                    "class2",
                                    "type2",
                                    12.0,
                                    12.0,
                                    12.0,
                                    "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.petbarn.com.au%2Fpetspot%2Fapp%2Fuploads%2F2019%2F01%2Fkitten-000017380158_Smaller.jpg&f=1&nofb=1&ipt=ed37c9a91356b71a8866925647ee12920ed16f623499c6597c8f7f42eb3529c9&ipo=images"
                                ),
                            ),
                            "11-01-2024"
                        )
                    )
                )
                _patient.postValue(Resource.success(patient))
            } catch (e: Throwable) {
                Log.d(TAG, "checkPatient: " + e.message)
                if (e is HttpException) {
                    if (e.code() == 404) {
                        _patient.postValue(Resource.error("Patient does not exist", null))
                    } else {
                        _patient.postValue(
                            Resource.error(
                                "An error occurred while getting patient data",
                                null
                            )
                        )
                    }
                } else {
                    _patient.postValue(
                        Resource.error(
                            "An error occurred while getting patient data",
                            null
                        )
                    )
                }
            }
        }
    }

    fun resetPatient() {
        _patient.postValue(null)
    }

}