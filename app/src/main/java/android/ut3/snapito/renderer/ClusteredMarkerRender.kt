package android.ut3.snapito.renderer

import android.content.Context
import android.ut3.snapito.model.maps.MyClusterItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class ClusteredMarkerRender(context: Context, map: GoogleMap, clusterManager: ClusterManager<MyClusterItem>)
    : DefaultClusterRenderer<MyClusterItem>(context, map, clusterManager) {

    override fun onBeforeClusterRendered(cluster: Cluster<MyClusterItem>?, markerOptions: MarkerOptions?) {
        super.onBeforeClusterRendered(cluster, markerOptions)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<MyClusterItem>?): Boolean {
        return cluster!!.size > 1
    }
}