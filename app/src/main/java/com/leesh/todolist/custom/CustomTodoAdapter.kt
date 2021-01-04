package com.leesh.todolist.custom

import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.leesh.todolist.R
import com.leesh.todolist.databinding.ItemTodoBinding

class CustomTodoAdapter(
    private var dataSet: List<DocumentSnapshot>,

    // 이걸 통해서 밖으로 Todo객체를 TodoAdapter 밖으로 전달할 것이다. -> Unit 리턴 받을 것 없다.
    val onClickDeleteIcon: (todo: DocumentSnapshot) -> Unit,
    val onClickItem: (todo: DocumentSnapshot) -> Unit
) :
    RecyclerView.Adapter<CustomTodoAdapter.TodoViewHolder>() {


    class TodoViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_todo, viewGroup, false)

        return TodoViewHolder(ItemTodoBinding.bind(view))
    }

    override fun onBindViewHolder(viewHolder: TodoViewHolder, position: Int) {
        val todo = dataSet[position]

        viewHolder.binding.todoText.text = todo.getString("text")?:""


        if ((todo.getBoolean("status")?:false) == true) { // status = true 할일 완료
            viewHolder.binding.todoText.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                setTypeface(null, Typeface.ITALIC) // 이탤릭체 처리
            }
        } else { // status = false 할일 해야함!!
            viewHolder.binding.todoText.apply {
                paintFlags = 0
                setTypeface(null, Typeface.NORMAL) // 노말 처리
            }
        }

        // 할일 삭제 버튼 클릭 이벤트 처리
        viewHolder.binding.deleteImageView.setOnClickListener {
            // dataSet 에 데이터가 삭제되었다는 것을 알려줘야 한다. 그리고 다시 notifyDataSetChanged
            onClickDeleteIcon.invoke(todo) // invoke: 는 함수를 실행한다는 의미
        }

        // 각 아이템 클릭 이벤트 처리
        // 전체는 root 로 접근 한다. 1 아이템 전면을 클릭이 가능하게 끔
        viewHolder.binding.root.setOnClickListener() {
            onClickItem.invoke(todo)
        }
    }

    override fun getItemCount() = dataSet.size



    /** MutableLiveData 를 사용하기 위해 만들어진 함수
     * MutableLiveData 은 데이터가 변경 될때 마다 새로 List를 재할당하게 설정되어 있다.
     * 이 함수를 호출하면 dataSet에 다시 List를 재할당하게 되는것.
     */
    fun setData(newData: List<DocumentSnapshot>) {
        dataSet = newData // List 재할당
        notifyDataSetChanged() // 데이터 갱신
    }
}


