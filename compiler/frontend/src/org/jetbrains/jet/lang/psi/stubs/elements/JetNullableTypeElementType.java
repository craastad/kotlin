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
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.psi.JetNullableType;
import org.jetbrains.jet.lang.psi.stubs.PsiJetPlaceHolderStub;

public class JetNullableTypeElementType extends JetPlaceHolderStubElementType<JetNullableType> {
    public JetNullableTypeElementType(@NotNull @NonNls String debugName) {
        super(debugName);
    }

    @Override
    public JetNullableType createPsiFromAst(@NotNull ASTNode node) {
        return new JetNullableType(node);
    }

    @Override
    public JetNullableType createPsi(@NotNull PsiJetPlaceHolderStub<JetNullableType> stub) {
        return new JetNullableType(stub);
    }

    @NotNull
    @Override
    protected JetPlaceHolderStubElementType<JetNullableType> getInstance() {
        return JetStubElementTypes.NULLABLE_TYPE;
    }
}