package com.leesh.todolist

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.leesh.todolist.databinding.ActivityMainBinding
import com.leesh.todolist.databinding.ItemTodoBinding

class MainActivity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName

    val rc_SIGN_IN = 1000;
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 로그인 상태 체크
        if (FirebaseAuth.getInstance().currentUser == null) {
            login()
        }


        // 임시 데이터 삽입
//        data.add(Todo("숙제"))
//        data.add(Todo("청소", true))

        // 위에도 같은 코드가 반복되어서 줄일수 있을것 같다~
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = TodoAdapter(
//                viewModel.data,
                emptyList(), // emptyList()( = 일단 에러가 나지 않게 코틀린에서 제공함.)
                onClickDeleteIcon = {
                    viewModel.deleteTodo(it)

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

        /**
         * 관찰하여 UI 업데이트 시키는 code
         * viewModel 의 todoLiveData 이 바뀔때 마다
         * it 으로 List<Todo> 가 넘어온다.
         * LiveData 를 사용하면 화면 갱신 하는 code 를 한쪽에 몰아 넣을수 있음 [연결되어 있는 분산된 코드를 한곳에 모아 놓을수 있음]
         * [분산]
         */
        viewModel.todoLiveData.observe(this, Observer {
            // 리사이클러뷰 어댑터를 불러오는 하위 TodoAdapter 를 => 상위 adapter 로 Typecasting(다형성) 하여 setData 함수를 불러옴
            (binding.recyclerView.adapter as TodoAdapter).setData(it)
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rc_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
//                val user = FirebaseAuth.getInstance().currentUser
                viewModel.fetchData()
            } else {
                // 로그인 실패
                finish()
            }
        }
    }

    fun login() {
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            rc_SIGN_IN
        )
    }

    fun logout() {
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

// 데이터 클래스로 사용할 클래스
data class Todo(
    val text: String,
    var isDone: Boolean = false, // 기본 값 false
)


/**
 * shift + F6 = 연관된 관련 객체명 모두 변경
 * TodoAdapter
 * TodoViewHolder
 *  데이터 세트: Array<Todo객체>
 *
 */
class TodoAdapter(
    // val -> var
    private var myDataset: List<DocumentSnapshot>,
    // 이걸 통해서 밖으로 Todo객체를 TodoAdapter 밖으로 전달할 것이다. -> Unit 리턴 받을 것 없다.
    val onClickDeleteIcon: (todo: DocumentSnapshot) -> Unit,
    val onClickItem: (todo: DocumentSnapshot) -> Unit
) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    // 뷰홀더 의 파라미터의 인자값(View view) // 바인딩 형태의 데이터와 UI 컴포넌트 연결
    // 뷰홀더는 뷰를 받는다. 모든 binding 객체는 binding.root 라는 프로퍼티 가 있어서 어떤 뷰로 부터 생겨난 바인딩 인지 추적할수가 있다.
    class TodoViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoAdapter.TodoViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)

        return TodoViewHolder(ItemTodoBinding.bind(view))
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = myDataset[position]
        holder.binding.todoText.text = todo.getString("text") ?: ""

        if ( (todo.getBoolean("isDone") ?: false) == true ) { // 할일이 완료된 경우
//            holder.binding.todoText.paintFlags = holder.binding.todoText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            // 우리는 코틀린으로 코딩을 하는 중이기 때문에 위의 긴 코드 중에서 중복되는 부분을 줄일수 있다. -> apply 함수 사용
            holder.binding.todoText.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                setTypeface(null, Typeface.ITALIC) // 이탤릭체 처리
            }
        } else {
            holder.binding.todoText.apply {
                paintFlags = 0
                setTypeface(null, Typeface.NORMAL) // 노말 처리
            }
        }


        holder.binding.deleteImageView.setOnClickListener {
            // myDataset 에 데이터가 삭제되었다는 것을 알려줘야 한다. 그리고 다시 notifyDataSetChanged
            onClickDeleteIcon.invoke(todo) // invoke: 는 함수를 실행한다는 의미
        }
        // 전체는 root 로 접근 한다. 1 아이템 전면을 클릭이 가능하게 끔
        holder.binding.root.setOnClickListener() {
            onClickItem.invoke(todo)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    /**
     * 이 함수를 호출하면 데이터가 변경된다.
     */
    fun setData(newData: List<DocumentSnapshot>) {
        myDataset = newData
        notifyDataSetChanged()
    }


}// TodoAdapter





/**
 * 뷰모델에서 데이터 관리
 */
class MainViewModel : ViewModel() {
    val db = Firebase.firestore
    val todoLiveData = MutableLiveData<List<DocumentSnapshot>>()

    init {
        fetchData()
    }

    fun fetchData(){
        val user = FirebaseAuth.getInstance().currentUser // 없을 수도 있기에 null 체크 해줘야 함.
        if (user != null) { // 유저가 null 아니라면 실행
            db.collection(user.uid) // collection 명 = user.uid
                // 자동으로 갱신되는 데이터
                .addSnapshotListener { value, e ->
                    if (e != null) {
                        Log.w("MainViewModel", "Listen failed.", e)
                        return@addSnapshotListener // 바로 종료
                    }

                    if (value != null){
                        todoLiveData.value = value.documents
                    }

                }
        }
    }

    fun toggleTodo(todo: DocumentSnapshot) {
//        todo.isDone = !todo.isDone
//        todoLiveData.value = data
    }
    fun addTodo(todo: Todo) {
        FirebaseAuth.getInstance().currentUser?. let { user ->//  currentUser? = 이것이 null 이 아닐때만 실행하게끔 하자
            db.collection(user.uid).add(todo)
                .addOnSuccessListener { documentReference ->
                    Log.d("data add Success", "DocumentSnapshot written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("data add Failure", "Error adding document", e)
                }
        }
    }
    fun deleteTodo(todo: DocumentSnapshot) {
        /**
         * 삭제를 위해서는 Document 의 uid 필요
         */
        FirebaseAuth.getInstance().currentUser?. let { user ->//  currentUser? = 이것이 null 이 아닐때만 실행하게끔 하자
            db.collection(user.uid).document(todo.id).delete()
                .addOnSuccessListener { Log.d("deleteTodo_success", "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w("deleteTodo_failure", "Error deleting document", e) }
        }
    }


}




















