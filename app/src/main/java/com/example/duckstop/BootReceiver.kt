import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.duckstop.MainActivity

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            // Start MainActivity on boot
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(mainActivityIntent)
        }
    }
}
