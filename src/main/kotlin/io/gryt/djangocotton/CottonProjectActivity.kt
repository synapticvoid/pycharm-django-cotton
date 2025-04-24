import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

class CottonProjectActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        println("Hello from CottonProjectActivity")
        if (!isDjangoCottonProject(project)) {
            return
        }

        showNotification(project, "Django cotton enabled for this project")

    }

    private fun isDjangoCottonProject(project: Project): Boolean {
        val componentDirs = listOf("cotton")
        val htmlFiles = FilenameIndex.getAllFilesByExt(project, "html", GlobalSearchScope.projectScope(project))

        for (file in htmlFiles) {
            val path = file.path.lowercase()
            if (componentDirs.any { path.contains(it) }) {
                return true
            }
        }

        return false
    }

    private fun showNotification(project: Project, message: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Cotton")
            .createNotification(message, NotificationType.INFORMATION)
            .notify(project)
    }
}