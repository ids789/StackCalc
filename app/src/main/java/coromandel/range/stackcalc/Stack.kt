package coromandel.range.stackcalc

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Stack(val context: Context, val stackView: RecyclerView) {

    val stackItems = mutableListOf<StackItem>()
    val stackAdapter: StackAdapter

    init {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd = true
        stackView.layoutManager = layoutManager

        stackAdapter = StackAdapter(stackItems)
        stackView.adapter = stackAdapter

        stackView.scrollToPosition(0)
    }

    fun push(value: Double) {
        insertItem(StackItem(value))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun push(operator: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val angleMode = sharedPreferences.getString("angle_units", "Radians")

        val result = Operation.exec(operator, stackItems.toList(), angleMode.equals("Degrees"))
        stackItems.subList(0, result.itemsTaken).clear()
        stackItems.addAll(0, result.items)
        stackAdapter.notifyDataSetChanged()
        when (result.type) {
            Operation.ResultType.CONVERSION_ERROR -> {
                Toast.makeText(context, R.string.unit_conversion_error, Toast.LENGTH_SHORT).show()
            }
            Operation.ResultType.INSUFFICIENT_ITEMS -> {
                Toast.makeText(context, R.string.insufficient_values_error, Toast.LENGTH_SHORT).show()
            }
            Operation.ResultType.UNIT_ERROR -> {
                Toast.makeText(context, R.string.unit_conversion_error, Toast.LENGTH_SHORT).show()
            }
            Operation.ResultType.MATHS_ERROR -> {
                Toast.makeText(context, R.string.maths_error, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    @SuppressLint("NotifyDataSetChanged") // need to update the formatting on every row on change
    fun cloneFirstItem() {
        if (stackItems.size > 0) {
            stackItems.add(0, stackItems.get(0))
            stackAdapter.notifyDataSetChanged()
            stackView.scrollToPosition(0)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteFirstItem() {
        if (stackItems.size > 0) {
            stackItems.removeAt(0)
            stackAdapter.notifyDataSetChanged()
            stackView.scrollToPosition(0)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearStack() {
        stackItems.clear()
        stackAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun insertItem(item: StackItem) {
        stackItems.add(0, item)
        stackAdapter.notifyDataSetChanged()
        stackView.scrollToPosition(0)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun takeItems(count: Int): List<StackItem> {
        val items = stackItems.subList(0, count).toMutableList()
        stackItems.subList(0, count).clear()
        stackAdapter.notifyDataSetChanged()
        return items
    }
}