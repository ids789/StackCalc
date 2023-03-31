package coromandel.range.stackcalc

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat


class StackAdapter(private val items: MutableList<StackItem>) : RecyclerView.Adapter<StackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stack_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)

        var value = items[position].value
        if (value.isInfinite() || value.isNaN()) {
            if (position == 0) {
                holder.valueTextView.textSize = 60F
                holder.unitTextView.textSize = 20F
            }
            holder.valueTextView.text = value.toString()
        } else if (value != 0.0 && (kotlin.math.abs(value) > 100000000000 || kotlin.math.abs(value) < 0.000000000001)) {
            var values = String.format("%.10g", value).split("e")
            holder.valueTextView.text = Html.fromHtml(values[0] + " <sup><small><small><small>e" + values[1] + "</small></small></small></sup>")
            if (position == 0) {
                holder.valueTextView.textSize = 35F
                holder.unitTextView.textSize = 20F
            }
        } else {
            val decFormat = DecimalFormat("#,###.#################")
            var plainString = decFormat.format(value)
            if (position == 0) {
                if (plainString.length > 13) {
                    holder.valueTextView.textSize = 25F
                } else if (plainString.length > 9) {
                    holder.valueTextView.textSize = 35F
                } else {123
                    holder.valueTextView.textSize = 60F
                }
                holder.unitTextView.textSize = 20F
            }
            holder.valueTextView.text = plainString
        }

        holder.unitTextView.text = items[position].unit.toString()

        holder.itemView.setOnClickListener {
            var item = items[position]
            if (position == 0) {1
                items.add(items.size, item);
                items.removeAt(position)
                notifyDataSetChanged()
            } else {
                items.add(0, item);
                items.removeAt(position + 1)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val valueTextView: TextView = itemView.findViewById(R.id.valueText)
        val unitTextView: TextView = itemView.findViewById(R.id.unitText)
    }
}