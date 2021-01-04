package com.leesh.todolist.custom

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


/**
 * Activity 에서 하고 있던 데이터 관리를
 * 모두 ViewModel로 옮긴다.
 */
class HomeViewModel : ViewModel() {
    val db = FirebaseFirestore.getInstance() // firestore 객체 얻기

    val todoLiveData = MutableLiveData<List<Todo>>()
    private val data = arrayListOf<Todo>()

    init {
        fetchData()
    }

    fun fetchData(){
        // 데이터 읽기
        db.collection("todos")
            .get()
            .addOnSuccessListener { result ->
                data.clear() // 데이터 비우고 다시 쌓는다
                for (document in result) {
                    // id = Document id
                    // data = 해당 Document의 모든 field 데이터
                    // Log.d(TAG, "${document.id} => ${document.data}")

                    val todo = Todo(
                        document.data["text"] as String,
                        document.data["isDone"] as Boolean,
                    )
                    data.add(todo)
                }
                todoLiveData.value = data
            }
            .addOnFailureListener { exception ->
                Log.w("실패시", "Error getting documents.", exception)
            }
    }

    // 토글
    fun toggleTodo(todo: Todo) {
        todo.isDone = !todo.isDone
        todoLiveData.value = data
    }
    // 추가
    fun addTodo(todo: Todo) {
        data.add(todo) // 데이터 추가 후에
        todoLiveData.value = data
    }
    // 삭제
    fun deleteTodo(todo: Todo) {
        data.remove(todo) // 데이터 삭제 후에 어댑터에 알려줘야 함.
        todoLiveData.value = data
    }


}