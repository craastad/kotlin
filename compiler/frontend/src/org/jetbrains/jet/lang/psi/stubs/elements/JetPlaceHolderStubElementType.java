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

import com.intellij.psi.stubs.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.psi.JetElement;
import org.jetbrains.jet.lang.psi.stubs.PsiJetPlaceHolderStub;
import org.jetbrains.jet.lang.psi.stubs.impl.PsiJetPlaceHolderStubImpl;

import java.io.IOException;

public abstract class JetPlaceHolderStubElementType<T extends JetElement> extends JetStubElementType<PsiJetPlaceHolderStub<T>, T> {
    public JetPlaceHolderStubElementType(@NotNull @NonNls String debugName) {
        super(debugName);
    }

    @Override
    public PsiJetPlaceHolderStub<T> createStub(@NotNull T psi, StubElement parentStub) {
        return new PsiJetPlaceHolderStubImpl<T>(parentStub, getInstance());
    }

    @Override
    public void serialize(@NotNull PsiJetPlaceHolderStub<T> stub, @NotNull StubOutputStream dataStream) throws IOException {
        //do nothing
    }

    @NotNull
    @Override
    public PsiJetPlaceHolderStub<T> deserialize(
            @NotNull StubInputStream dataStream, StubElement parentStub
    ) throws IOException {
        return new PsiJetPlaceHolderStubImpl<T>(parentStub, getInstance());
    }

    @Override
    public void indexStub(@NotNull PsiJetPlaceHolderStub<T> stub, @NotNull IndexSink sink) {
        //do nothing
    }

    @NotNull
    protected abstract JetPlaceHolderStubElementType<T> getInstance();
}