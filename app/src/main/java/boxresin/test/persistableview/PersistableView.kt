package boxresin.test.persistableview

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.EditText

class PersistableView(context: Context) : EditText(context) {
    init {
        // id 를 지정해야 onSaveInstanceState()가 호출된다.
        id = 1
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        Log.v("ASDF", "onRestoreInstanceState($state), id = $id")

        if (state !is SavedState) {
            return super.onRestoreInstanceState(state)
        }

        super.onRestoreInstanceState(state.superState)
        Log.v("ASDF", "state.savedText = ${state.savedText}")

        // View 상태 복구
        this.setText(state.savedText)
    }

    override fun onSaveInstanceState(): Parcelable? {
        Log.v("ASDF", "onSaveInstanceState(), id = $id")

        // 커스텀 Parcelable 저장
        return SavedState(super.onSaveInstanceState())
    }

    private inner class SavedState : BaseSavedState {
        /** 저장된 텍스트 */
        val savedText: String

        /** 직접 객체 생성 */
        constructor(source: Parcelable?) : super(source) {
            savedText = this@PersistableView.text.toString()
        }

        /** [Parcel]로부터 객체 생성 */
        constructor(`in`: Parcel) : super(`in`) {
            this.savedText = `in`.readString() ?: ""
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)

            // 커스텀 필드 저장
            out.writeString(savedText)
        }

        /** Parcel로부터 Parcelable을 만들기 위한 프로퍼티 */
        @JvmField
        val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
            override fun createFromParcel(`in`: Parcel): SavedState {
                return SavedState(`in`)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}
