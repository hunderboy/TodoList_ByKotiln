package com.leesh.todolist.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.leesh.todolist.R
import kotlinx.android.synthetic.main.fragment_order_bottom_dialog.view.*

class OrderBottomDialogFragment(val itemClick: (Int) -> Unit) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_calendar_day_exercise, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        view.tv_recommend_order.setOnClickListener {
//            itemClick(0)
//            dialog?.dismiss()
//        }
//        view.tv_review_order.setOnClickListener {
//            itemClick(1)
//            dialog?.dismiss()
//        }
    }
}