package comm.comquas.ursmskit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import comm.comquas.ursmskit.model.CountryListModel
import comm.comquas.ursmskit.uty.rec.MyAdapterOnLoadMoreListener
import comm.comquas.ursmskit.uty.rec.MyRecyclerViewRowClickListener
import comm.comquas.ursmskit.uty.rec.setSafeOnClickListener

class CountryAdapter
    (
    private var list: List<CountryListModel.Data>,
    private var rowClickListener: MyRecyclerViewRowClickListener
) :
    androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private val typeData = 0
    private val typeLoad = 1
    private var loadMoreListener: MyAdapterOnLoadMoreListener? = null
    private var isLoading = false
    private var isMoreDataAvailable = true

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == typeData) {
            MyViewHolder(
                inflater.inflate(R.layout.viewholder_country_more, parent, false),
                rowClickListener
            )
        } else {
            LoadHolder(inflater.inflate(R.layout.viewholder_load, parent, false))
        }
    }

    override fun onBindViewHolder(
        holder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        position: Int
    ) {

        if (position >= itemCount - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
            isLoading = true
            loadMoreListener!!.onLoadMore()
        }

        if (getItemViewType(position) == typeData) {
            (holder as MyViewHolder).bindData(list[position], position)
        }
        holder.setIsRecyclable(false)
    }

    override fun getItemViewType(position: Int): Int {
        return typeData
    }

    override fun getItemCount(): Int {
        return list.size
    }

    internal class MyViewHolder(itemView: View, rowClickListener: MyRecyclerViewRowClickListener?) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        private var tv1 = itemView.findViewById<View>(R.id.textView) as TextView
        private var ivBanner = itemView.findViewById<View>(R.id.ivBanner) as ImageView

        init {
            itemView.setSafeOnClickListener {
                rowClickListener?.onRowClicked(adapterPosition)
            }
        }

        fun bindData(dataModel: CountryListModel.Data, position: Int) {
            tv1.text = "${dataModel.cou5h2ER7} (${dataModel.coUBhK4K})"
            if (!dataModel.phQr4Q2d.isNullOrEmpty())
                Picasso.get().load(dataModel.phQr4Q2d)
                    .into(ivBanner)
        }
    }

    internal class LoadHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)
}