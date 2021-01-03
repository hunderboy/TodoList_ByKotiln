package com.leesh.todolist.custom

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


/**
 * Activity 에서 하고 있던 데이터 관리를
 * 모두 ViewModel로 옮긴다.
 */
class HomeViewModel : ViewModel() {

    val todoLiveData = MutableLiveData<List<Todo>>()


    private val data = arrayListOf<Todo>()


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