package com.leesh.todolist.custom

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase


/**
 * Activity 에서 하고 있던 데이터 관리를
 * 모두 ViewModel로 옮긴다.
 */
class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance() // firestore 객체 얻기

    val todoLiveData = MutableLiveData<List<DocumentSnapshot>>()
//    private val data = arrayListOf<QueryDocumentSnapshot>() // 각 document 들의 리스트..  = 필요없어짐

    init {
        fetchData()
    }

    fun fetchData(){
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null) {
            // 데이터 읽기
            db.collection(user.uid) // 로그인 유저의 Uid 라고 설정된 collection 접근
                /** 여기서 사실상
                value(QuerySnapshot) = 컬렉션(Table 역할)
                document(QueryDocumentSnapshot) = 문서(row 역할)
                 */
                .addSnapshotListener { value, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }

                    if(value != null){
                        todoLiveData.value = value.documents // 도큐먼트 들!!! = documents
                    }

                    // 필요 없어짐
//                    data.clear() // 데이터 비우고 다시 쌓는다
//                    for (document in value!!) {
//                        data.add(document)
//                    }
//                    todoLiveData.value = data
                }
        }
    }

    // 토글
    fun toggleTodo(todo: DocumentSnapshot) {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            val isDone = todo.getBoolean("isDone")?:false
            db.collection(user.uid).document(todo.id).update("isDone", !isDone) // key, value
        }
    }
    // 추가
    fun addTodo(todo: Todo) {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            db.collection(user.uid).add(todo)
        }
    }
    // 삭제
    fun deleteTodo(todo: DocumentSnapshot) {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            // 삭제를 위해서는 Document.id 가 필요하다.
            db.collection(user.uid).document(todo.id).delete()
        }
    }


}