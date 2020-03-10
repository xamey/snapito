package android.ut3.snapito.helpers

import android.ut3.snapito.model.maps.MyClusterItem
import com.google.maps.android.clustering.Cluster

fun checkIfClusterItemsAtSamePosition(cluster: Cluster<MyClusterItem>?): Boolean {
    var item = cluster?.items?.elementAt(0)
    return (cluster?.items?.filter { s -> item?.position == s.position }?.size == cluster?.size)
}