package com.leesh.todolist.custom

// 데이터 클래스로 사용할 클래스
data class Todo(
    val text: String,
    var isDone: Boolean = false, // 기본 값 false
)