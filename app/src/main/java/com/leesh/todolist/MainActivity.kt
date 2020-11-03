package com.leesh.todolist

import android.graphics.Paint
import android.graphics.Typeface
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

    // 데이터 리스트
    /**
     * 화면 회전 시에 생명주기에 의해서 기존화면이 파괴 되고 재생성된다.
     * 그래서 구글에서 이러한 현상을 쉽게 해결해 주는 ViewModel 을 사용핼 볼꺼야~
     */
    private val data = arrayListOf<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩으로 수정
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // 임시 데이터 삽입
//        data.add(Todo("숙제"))
//        data.add(Todo("청소", true))

        // 리사이클러뷰 선언
//        binding.recyclerView.layoutManager = LinearLayoutManager(this)
//        binding.recyclerView.adapter = TodoAdapter(data,
//            onClickDeleteIcon = {
//                deleteTodo(it)
//            }
//        )
        // 위에도 같은 코드가 반복되어서 줄일수 있을것 같다~
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = TodoAdapter(
                data,
                onClickDeleteIcon = {
                    deleteTodo(it)
                },
                onClickItem = {
                    toggleTodo(it)
                }
            )
        }


//        viewManager = LinearLayoutManager(this)
//        viewAdapter = TodoAdapter(myDataset)
//
//        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
//            // use this setting to improve performance if you know that changes
//            // in content do not change the layout size of the RecyclerView
//
//            /*** // 사이즈가 변할일이 없을때는 성능이 좋아지는 건데... // 추가 삭제를 할거기 때문에 쓸일이 없다. */
//            setHasFixedSize(true)
//
//            // use a linear layout manager
//            layoutManager = viewManager
//
//            // specify an viewAdapter (see also next example)
//            adapter = viewAdapter


        binding.addButton.setOnClickListener {
            addTodo() // 추가 메소드 실행
        }
    }

    private fun toggleTodo(todo: Todo) {
        todo.isDone = !todo.isDone
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }


    private fun addTodo() {
        val todo = Todo(binding.editText.text.toString()) // isDone 은 false 가 기본값
        data.add(todo) // 데이터 추가 후에
        // 어댑터에 알려줘야 함.
        binding.recyclerView.adapter?.notifyDataSetChanged() // adapter? = null 일수 없게 방지한다.
    }

    private fun deleteTodo(todo: Todo) {
        data.remove(todo) // 데이터 삭제 후에 어댑터에 알려줘야 함.
        binding.recyclerView.adapter?.notifyDataSetChanged() // adapter? = null 일수 없게 방지한다.
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
    private val myDataset: List<Todo>,
    // 이걸 통해서 밖으로 Todo객체를 TodoAdapter 밖으로 전달할 것이다. -> Unit 리턴 받을 것 없다.
    val onClickDeleteIcon: (todo: Todo) -> Unit,
    val onClickItem: (todo: Todo) -> Unit
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
//        val textView = holder.view.findViewById<TextView>(R.id.todo_text) // view binding(뷰 바인딩) 적용할 예정
//        textView.text = myDataset[position].text

        val todo = myDataset[position]

        // 아래 코드로 바인딩 설정
        holder.binding.todoText.text = myDataset[position].text

        if (todo.isDone) { // 할일이 완료된 경우
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
}
