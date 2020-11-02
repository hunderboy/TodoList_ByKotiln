package com.leesh.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leesh.todolist.databinding.ActivityMainBinding
import com.leesh.todolist.databinding.ItemTodoBinding

class MainActivity : AppCompatActivity() {
    /**
     *  // build.gradle 에서 binding 을 설정해주면
        // 각 화면 마다의 binding이 기본적으로 생성되어 있다.
        // MainActivity => ActivityMainBinding
     */
    private lateinit var binding: ActivityMainBinding


//    private lateinit var recyclerView: RecyclerView
//    private lateinit var viewAdapter: RecyclerView.Adapter<*>
//    private lateinit var viewManager: RecyclerView.LayoutManager

    // 임시 데이터
    private val data = arrayListOf<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩으로 수정
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        data.add(Todo("숙제", false))
        data.add(Todo("청소", false))


        // 리사이클러뷰 선언
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = TodoAdapter(data)



//        viewManager = LinearLayoutManager(this)
//        viewAdapter = TodoAdapter(myDataset)
//
//        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
//            // use this setting to improve performance if you know that changes
//            // in content do not change the layout size of the RecyclerView
//            /*** // 사이즈가 변할일이 없을때는 성능이 좋아지는 건데... // 추가 삭제를 할거기 때문에 쓸일이 없다. */
//            setHasFixedSize(true)
//
//            // use a linear layout manager
//            layoutManager = viewManager
//
//            // specify an viewAdapter (see also next example)
//            adapter = viewAdapter

    }
}

// 데이터 클래스로 사용할 클래스
data class Todo(val text: String, var isDone : Boolean)


/**
 * shift + F6 = 연관된 관련 객체명 모두 변경
 * TodoAdapter
 * TodoViewHolder
 *  데이터 세트: Array<Todo객체>
 *
 */
class TodoAdapter(private val myDataset: List<Todo>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    // 뷰홀더 의 파라미터의 인자값(View view) // 바인딩 형태의 데이터와 UI 컴포넌트 연결
    // 뷰홀더는 뷰를 받는다. 모든 binding 객체는 binding.root 라는 프로퍼티 가 있어서 어떤 뷰로 부터 생겨난 바인딩 인지 추적할수가 있다.
    class TodoViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root)



    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): TodoAdapter.TodoViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)

        return TodoViewHolder(ItemTodoBinding.bind(view))
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {

//        val textView = holder.view.findViewById<TextView>(R.id.todo_text) // view binding(뷰 바인딩) 적용할 예정
//        textView.text = myDataset[position].text
        // 아래 코드로 바인딩 설정
        holder.binding.todoText.text = myDataset[position].text

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}
