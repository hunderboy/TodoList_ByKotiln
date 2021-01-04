package com.leesh.todolist.custom

// 데이터 클래스로 사용할 클래스
data class Todo(
    val text: String,
    var status: Boolean = false, // 기본 값 false
)