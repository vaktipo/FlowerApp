import android.os.Parcel
import android.os.Parcelable

data class Event(
    val eventName: String,
    val eventDate: String,
    val recurrence: String,
    val beneficiary: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(eventName)
        parcel.writeString(eventDate)
        parcel.writeString(recurrence)
        parcel.writeString(beneficiary)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }
    }
}