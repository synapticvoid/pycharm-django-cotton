package com.djcotton

import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlTagNameProvider
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.ui.IconManager
import javax.swing.Icon

/**
 * Provides custom tag name recognition for django-cotton components.
 * This class allows PyCharm to recognize custom component tags without showing warnings.
 */
class CottonTagContributor : XmlTagNameProvider {

    override fun addTagNameVariants(elements: MutableList<LookupElement>, tag: XmlTag, prefix: String) {
        val project = tag.project

        // Only add components in Django template files
        if (!isDjangoTemplateFile(tag.containingFile.virtualFile)) {
            return
        }

        // Find all potential component files in the project
        val componentFiles = findComponentFiles(project)

        // Create lookup elements for each component
        for (componentFile in componentFiles) {
            // Derive component name from file name (e.g., group_card.html -> c-group-card)
            val fileName = componentFile.nameWithoutExtension
            val componentName = "c-${fileName.replace("_", "-")}"

            // Add component to autocompletion list
            elements.add(LookupElementBuilder.create(componentName)
                .withIcon(getComponentIcon())
                .withTypeText("Django Cotton Component")
                .withInsertHandler { context, _ ->
                    // Optional: Add common attributes when inserting the tag
                    // You can implement custom insert handling here
                })
        }
    }

    /**
     * Check if the file is a Django template file
     */
    private fun isDjangoTemplateFile(file: VirtualFile): Boolean {
        return file.extension == "html" || file.extension == "django" ||
                file.extension == "djhtml" || file.extension == "jinja2"
    }

    /**
     * Find potential component files in the project
     */
    private fun findComponentFiles(project: Project): List<VirtualFile> {
        // Look for html files in the templates/components directory
        val componentsDir = listOf("components", "partials")
        val componentFiles = mutableListOf<VirtualFile>()

        // Search for HTML files that could be components
        val htmlFiles = FilenameIndex.getAllFilesByExt(project, "html", GlobalSearchScope.projectScope(project))

        // Filter files that are likely components
        for (file in htmlFiles) {
            val path = file.path.toLowerCase()
            if (componentsDir.any { path.contains("/$it/") || path.contains("\\$it\\") }) {
                componentFiles.add(file)
            }
        }

        return componentFiles
    }

    /**
     * Get icon for the component
     */
    private fun getComponentIcon(): Icon? {
        // You could use a custom icon here
        return null // Use default icon
    }
}