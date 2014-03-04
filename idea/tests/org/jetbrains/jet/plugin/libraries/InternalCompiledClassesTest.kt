/*
 * Copyright 2010-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.plugin.libraries

import org.jetbrains.jet.plugin.JetLightCodeInsightFixtureTestCase
import com.intellij.testFramework.LightProjectDescriptor
import org.jetbrains.jet.plugin.JdkAndMockLibraryProjectDescriptor
import org.jetbrains.jet.plugin.PluginTestCaseBase
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jet.lang.resolve.java.JvmAbi
import com.intellij.psi.PsiManager
import junit.framework.Assert
import org.jetbrains.jet.lang.resolve.java.PackageClassUtils
import com.intellij.psi.ClassFileViewProvider
import com.intellij.psi.impl.compiled.ClsFileImpl
import com.intellij.psi.PsiCompiledFile
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile

//TODO: test for local functions and classes
public class InternalCompiledClassesTest : JetLightCodeInsightFixtureTestCase() {

    private val TEST_DATA_PATH = PluginTestCaseBase.getTestDataPathBase() + "/libraries/internalClasses"

    fun testPackagePartIsInvisible() = doTestNoPsiFilesAreBuiltFor("package part") {
        getNameWithoutExtension().contains(PackageClassUtils.PACKAGE_CLASS_NAME_SUFFIX + "-")
    }

    fun testAnonymousFunctionIsInvisible() = doTestNoPsiFilesAreBuiltFor("anonymous function") {
        isAnonymousFunction(this)
    }

    fun testInnerClassIsInvisible() = doTestNoPsiFilesAreBuiltFor("inner or nested class") {
        ClassFileViewProvider.isInnerClass(this)
    }

    fun testTraitImplClassIsVisibleAsJavaClass() {
        val project = getProject()!!
        doTest("trait impl", { getNameWithoutExtension().endsWith(JvmAbi.TRAIT_IMPL_SUFFIX) }) {
            val psiFile = PsiManager.getInstance(project).findFile(this)
            Assert.assertTrue("Should not be kotlin file",
                              psiFile !is JetClsFile)
            Assert.assertTrue("Should be java file, was ${psiFile!!.getClass().getSimpleName()}",
                              psiFile is ClsFileImpl)

            val decompiledPsiFile = (psiFile as PsiCompiledFile).getDecompiledPsiFile()!!
            Assert.assertTrue("Should be java decompiled file, was ${decompiledPsiFile.getClass().getSimpleName()}",
                              decompiledPsiFile is PsiJavaFile)
            val classes = (decompiledPsiFile as PsiJavaFile).getClasses()
            Assert.assertTrue("Should have some decompiled text",
                              classes.size == 1 && classes[0].getName()!!.endsWith(JvmAbi.TRAIT_IMPL_SUFFIX))
        }
    }

    override fun getProjectDescriptor(): LightProjectDescriptor {
        return JdkAndMockLibraryProjectDescriptor(TEST_DATA_PATH, withSources = false)
    }

    private fun doTestNoPsiFilesAreBuiltFor(fileKind: String, acceptFile: VirtualFile.() -> Boolean) {
        val project = getProject()!!
        doTest(fileKind, acceptFile) {
            val psiFile = PsiManager.getInstance(project).findFile(this)
            Assert.assertNull("PSI files for $fileKind classes should not be build, is was build for: ${this.getPresentableName()}",
                              psiFile)

        }
    }

    private fun doTest(fileKind: String, acceptFile: VirtualFile.() -> Boolean, performTest: VirtualFile.() -> Unit) {
        val root = NavigateToDecompiledLibraryTest.findTestLibraryRoot(myModule!!)!!
        var foundAtLeastOneFile = false
        root.checkRecursively {
            if (acceptFile()) {
                foundAtLeastOneFile = true
                performTest()
            }
        }
        Assert.assertTrue("Should find at least one file of kind ($fileKind). This assertion can fail in following scenarios:\n" +
                          "1. Test data is bad and doesn't cover this case.\n2. ABI has changed and test no longer checks anything.",
                          foundAtLeastOneFile)
    }

    private fun VirtualFile.checkRecursively(body: VirtualFile.() -> Unit) {
        if (!isDirectory()) {
            body()
        }
        else {
            for (file in getChildren()!!) {
                file.checkRecursively(body)
            }
        }
    }
}
