package android.ut3.snapito.model.firestore

data class StoredPhoto(
    var title: String = "",
    var lat: Double = 0.0,
    var long: Double = 0.0
) {
    override fun equals(other: Any?): Boolean {
        return this === other
    }
}

