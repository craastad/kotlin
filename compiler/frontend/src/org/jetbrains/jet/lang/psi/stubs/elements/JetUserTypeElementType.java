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

package org.jetbrains.jet.lang.psi.stubs.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.psi.JetUserType;
import org.jetbrains.jet.lang.psi.stubs.PsiJetUserTypeStub;
import org.jetbrains.jet.lang.psi.stubs.impl.PsiJetUserTypeStubImpl;

import java.io.IOException;

public class JetUserTypeElementType extends JetStubElementType<PsiJetUserTypeStub, JetUserType> {
    public JetUserTypeElementType(@NotNull @NonNls String debugName) {
        super(debugName);
    }

    @Override
    public JetUserType createPsiFromAst(@NotNull ASTNode node) {
        return new JetUserType(node);
    }

    @Override
    public JetUserType createPsi(@NotNull PsiJetUserTypeStub stub) {
        return new JetUserType(stub);
    }

    @Override
    public PsiJetUserTypeStub createStub(@NotNull JetUserType psi, StubElement parentStub) {
        //TODO:
        return new PsiJetUserTypeStubImpl(parentStub);
    }

    @Override
    public void serialize(@NotNull PsiJetUserTypeStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        //TODO:
    }

    @NotNull
    @Override
    public PsiJetUserTypeStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        //tODO:
        return new PsiJetUserTypeStubImpl(parentStub);
    }

    @Override
    public void indexStub(@NotNull PsiJetUserTypeStub stub, @NotNull IndexSink sink) {
        //do nothing
    }
}