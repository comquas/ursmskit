package comm.comquas.ursmskit.uty.rec

import android.view.View

interface MyRecyclerViewRowClickListener {
    fun onRowClicked(position: Int)
}

interface MyRecyclerViewRowClickListener2 {
    fun onRowClicked(position: Int)

    fun onViewClicked(v: View, position: Int)
}

