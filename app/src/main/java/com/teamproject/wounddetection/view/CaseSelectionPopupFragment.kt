package com.teamproject.wounddetection.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamproject.wounddetection.data.model.Case
import com.teamproject.wounddetection.databinding.FragmentCaseSelectionPopupBinding
import com.teamproject.wounddetection.utils.Status
import com.teamproject.wounddetection.viewmodel.AppViewModelProvider
import com.teamproject.wounddetection.viewmodel.CaseSelectionPopupViewModel

class CaseSelectionPopupFragment : DialogFragment() {

    private lateinit var binding: FragmentCaseSelectionPopupBinding

    private lateinit var adapter: CaseAdapter

    private val viewModel: CaseSelectionPopupViewModel by viewModels { AppViewModelProvider.Factory }

    private val args: CaseSelectionPopupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCaseSelectionPopupBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.isEmpty()) {
            viewModel.getCases(args.patientCode)
        }
        setupView()
        setupObserver()
    }


    private fun setupView() {
        binding.apply {
            rvCasesPopup.layoutManager = LinearLayoutManager(requireContext())
            adapter = CaseAdapter(mutableListOf(), ::onCaseClick)
            rvCasesPopup.adapter = adapter
            pullToRefresh.setOnRefreshListener { viewModel.getCases(args.patientCode) }
        }
    }

    private fun setupObserver() {
        viewModel.cases.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.pullToRefresh.isRefreshing = true
                }
                Status.SUCCESS -> {
                    binding.pullToRefresh.isRefreshing = false
                    it.data?.let { it1 -> updateData(it1) }
                }
                Status.ERROR -> {
                    binding.pullToRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onCaseClick(case: Case) {
        // TODO: upload photo to case
    }

    private fun updateData(cases: List<Case>) {
        adapter.clear()
        adapter.addData(cases)
        adapter.notifyDataSetChanged()
    }
}