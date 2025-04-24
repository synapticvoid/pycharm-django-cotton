import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class CottonProjectActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        println("Hello from CottonProjectActivity")
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Cotton")
            .createNotification("Hello", NotificationType.INFORMATION)
            .notify(project)
    }
}