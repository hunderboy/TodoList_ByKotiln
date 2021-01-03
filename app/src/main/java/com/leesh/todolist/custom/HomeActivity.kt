package com.leesh.todolist.custom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.leesh.todolist.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding


    private val data = arrayListOf<Todo>()

    //    implementation "androidx.fragment:fragment-ktx:1.2.5" // ViewModel 적용을 위해 필요함
    // jvm target 1.8 버전 이상이 필요함.
    //    kotlinOptions {
    //        jvmTarget = "1.8"
    //    }
    private val viewModel: HomeViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


//        binding.recyclerView.layoutManager = LinearLayoutManager(this)
//        binding.recyclerView.adapter = CustomTodoAdapter(data)

        // binding.recyclerView 가 반복된다.
        // 반복되는 코드는 apply 를 통해서 처리할수 있다.
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = CustomTodoAdapter(
//                emptyList(), // emptyList()( = 일단 에러가 나지 않게 코틀린에서 제공함.) == 잠시 테스트 할때 사용. 인자값이 List 일때만 허용됨
                viewModel.data,
                onClickDeleteIcon = {
                    viewModel.deleteTodo(it)
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                },
                onClickItem = {
                    viewModel.toggleTodo(it)
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                }
            )
        }


        binding.addButton.setOnClickListener {
            val todo = Todo(binding.editText.text.toString())
            viewModel.addTodo(todo)
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }


    }


}