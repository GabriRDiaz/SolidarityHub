import android.app.AlertDialog
import android.content.Context
import com.upv.solidarityHub.utils.builder.BaseDialogBuilder

class InfoDialogBuilder(context: Context) : BaseDialogBuilder(context) {
    override fun setTitle(title: String) {
        builder = AlertDialog.Builder(context)
        builder.setTitle(title)
    }

    override fun setMessage(message: String) {
        builder.setMessage(message)
    }

    override fun setIcon() {
        builder.setIcon(android.R.drawable.ic_dialog_info)
    }

    override fun setButton() {
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
    }
}
