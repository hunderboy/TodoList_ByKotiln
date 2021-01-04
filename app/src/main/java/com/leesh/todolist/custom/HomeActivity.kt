package com.leesh.todolist.custom

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.leesh.todolist.R
import com.leesh.todolist.TodoAdapter
import com.leesh.todolist.databinding.ActivityHomeBinding
import java.util.Observer

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    // 1. implementation "androidx.fragment:fragment-ktx:1.2.5" // ViewModel 적용을 위해 필요함
    // 2. jvm target 1.8 버전 이상이 필요함.
    //    kotlinOptions {
    //        jvmTarget = "1.8"
    //    }
    private val viewModel: HomeViewModel by viewModels()
    private val rcSignIn = 1000; // 로그인


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        // 로그인 상태 를 체크하는 것
        // 현재 사용자 == null 이면
        if (FirebaseAuth.getInstance().currentUser == null) {
            login() // 로그인 프로세스 처리
        }

//        binding.recyclerView.layoutManager = LinearLayoutManager(this)
//        binding.recyclerView.adapter = CustomTodoAdapter(data)

        // binding.recyclerView 가 반복된다.
        // 반복되는 코드는 apply 를 통해서 처리할수 있다.
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = CustomTodoAdapter(
                emptyList(), // emptyList()( = 일단 에러가 나지 않게 코틀린에서 제공함.) == 잠시 테스트 할때 사용. 인자값이 List 일때만 허용됨
//                viewModel.data,
                onClickDeleteIcon = {
                    viewModel.deleteTodo(it)
                },
                onClickItem = {
                    viewModel.toggleTodo(it)
                }
            )
        }


        binding.addButton.setOnClickListener {
            val todo = Todo(binding.editText.text.toString())
            viewModel.addTodo(todo)
        }

        // 관찰 UI 업데이트
        /**
         * 관찰하여 UI 업데이트 시키는 code
         * viewModel 의 todoLiveData 이 바뀔때 마다, it 으로 List<Todo> 가 넘어온다.
         * LiveData 를 사용하면 화면 갱신 하는 code 를 한쪽에 몰아 넣을수 있음 [연결되어 있는 분산된 코드를 한곳에 모아 놓을수 있음]
         */
        viewModel.todoLiveData.observe(this, androidx.lifecycle.Observer {
            (binding.recyclerView.adapter as CustomTodoAdapter).setData(it)
        })

    }// onCreate







    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcSignIn) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // 로그인 성공 시
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, "접속 유저의 email"+ user?.email.toString(), Toast.LENGTH_SHORT).show()
                viewModel.fetchData() // 리스트 재설정

            } else {
                // 로그인 실패
                finish()
            }
        }
    }

    // 로그인 처리
    fun login(){
        // 파이어 베이스 자체적으로 가지고 있는, 이메일 인증 UI
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers) // 이메일 인증 UI 화면 표시
                .build(),
            rcSignIn) // response 값
    }
    fun logout(){
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // 로그아웃을 하면 로그인을 화면을 다시 띄운다.
                login()
            }
    }

    /**
     * 메뉴의 로그아웃
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_log_out -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }




}