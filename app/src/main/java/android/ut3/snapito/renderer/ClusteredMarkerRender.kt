package android.ut3.snapito.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.ut3.snapito.R
import android.ut3.snapito.helpers.checkIfClusterItemsAtSamePosition
import android.ut3.snapito.model.maps.MyClusterItem
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

class ClusteredMarkerRender(context: Context, map: GoogleMap, clusterManager: ClusterManager<MyClusterItem>)
    : DefaultClusterRenderer<MyClusterItem>(context, map, clusterManager) {
    var mContext = context

    override fun onBeforeClusterRendered(cluster: Cluster<MyClusterItem>?, markerOptions: MarkerOptions?) {
        super.onBeforeClusterRendered(cluster, markerOptions)

        if (checkIfClusterItemsAtSamePosition(cluster)) {
            var drawable: Drawable? = ContextCompat.getDrawable(mContext, R.drawable.ic_multiple_photos_foreground)
            drawable!!.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            var bitmap: Bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            var canvas = Canvas(bitmap)
            drawable.draw(canvas)
            markerOptions!!.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        }
    }

    override fun onBeforeClusterItemRendered(item: MyClusterItem?, markerOptions: MarkerOptions?) {
        super.onBeforeClusterItemRendered(item, markerOptions)
        var drawable: Drawable? = ContextCompat.getDrawable(mContext, R.drawable.ic_single_photos_foreground)
        drawable!!.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        var bitmap: Bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        drawable.draw(canvas)
        markerOptions!!.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
    }



    override fun shouldRenderAsCluster(cluster: Cluster<MyClusterItem>?): Boolean {
        return cluster!!.size > 1
    }
}