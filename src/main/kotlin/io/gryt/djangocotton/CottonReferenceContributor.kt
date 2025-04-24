package io.gryt.djangocotton

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.XmlPatterns
import com.intellij.psi.*
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlTag
import com.intellij.util.ProcessingContext

class CottonReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            XmlPatterns.xmlTag().withName(PlatformPatterns.string().startsWith("c-")),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement,
                    context: ProcessingContext
                ): Array<out PsiReference?> {
                    if (element !is XmlTag) {
                        return PsiReference.EMPTY_ARRAY
                    }

                    // c-group-card => group-card
                    val componentName = element.name.substring(2).replace("-", "_")
                    val reference = ComponentReference(element, componentName, element.textRange)
                    return arrayOf(reference)
                }

            }
        )
    }
}

private class ComponentReference(element: PsiElement, private val componentName: String, textRange: TextRange) :
    PsiReferenceBase<PsiElement>(element, textRange, true) {
    override fun resolve(): PsiElement? {
        val project = element.project

        // Find the corresponding component file and convert to PsiFile (for navigation)
        val componentFile = findComponentFile(project, componentName) ?: return null
        val psiFile = PsiManager.getInstance(project).findFile(componentFile) ?: return null

        return psiFile
    }

    override fun getVariants(): Array<Any> = emptyArray()

    private fun findComponentFile(project: Project, componentName: String): VirtualFile? {
        val fileName = "$componentName.html"
        val componentDirs = listOf("cotton")

        val files = FilenameIndex.getVirtualFilesByName(fileName, GlobalSearchScope.projectScope(project))
        for (file in files) {
            val path = file.path.lowercase()
            // FIXME factorize matching with CottonProjectActivity
            if (componentDirs.any { path.contains(it) }) {
                println("Matched component. componentName=$componentName, path=$path")
                return file
            }
        }
        return null
    }

}