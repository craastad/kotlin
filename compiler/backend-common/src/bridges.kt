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

package org.jetbrains.jet.codegen.bridges

import org.jetbrains.jet.utils.DFS
import java.util.HashSet
import org.jetbrains.jet.lang.descriptors.FunctionDescriptor
import org.jetbrains.jet.lang.descriptors.Modality

/**
 * Represents Kotlin functions, i.e. it's an abstraction over FunctionDescriptor
 */
public trait FunctionHandle {
    val isDeclaration: Boolean
    val isAbstract: Boolean

    fun getOverridden(): Iterable<FunctionHandle>
}

public data class Bridge<Method>(
        public val from: Method,
        public val to: Method
) {
    override fun toString() = "$from -> $to"
}


public fun <Method> generateBridgesForFunctionDescriptor(descriptor: FunctionDescriptor, mapToMethod: (FunctionDescriptor) -> Method): Set<Bridge<Method>> {
    return generateBridges(DescriptorBasedFunctionHandle(descriptor), { mapToMethod(it.descriptor) })
}

private data class DescriptorBasedFunctionHandle(val descriptor: FunctionDescriptor) : FunctionHandle {
    override val isDeclaration: Boolean = descriptor.getKind().isReal()
    override val isAbstract: Boolean = descriptor.getModality() == Modality.ABSTRACT

    override fun getOverridden(): Iterable<FunctionHandle> {
        return descriptor.getOverriddenDescriptors().map { DescriptorBasedFunctionHandle(it.getOriginal()) }
    }
}


public fun <Function : FunctionHandle, Method> generateBridges(function: Function, mapToMethod: (Function) -> Method): Set<Bridge<Method>> {
    if (function.isDeclaration) {
        val method = mapToMethod(function)
        return findAllReachableDeclarations(function, mapToMethod).filter { it != method }.map { Bridge(it, method) }.toSet()
    }

    // If it's an abstract fake override, no bridges are needed: when an implementation will appear in some subclass, all necessary
    // bridges will be generated there
    if (function.isAbstract) return setOf()

    // If it's a concrete fake override and all of its super-functions are concrete, then every possible bridge is already generated
    // into some of the super-classes and will be inherited in this class
    if (function.getOverridden().none { it.isAbstract }) return setOf()

    val implementation = findNonAbstractDeclaration(function)

    val bridgesToGenerate = findAllReachableDeclarations(function, mapToMethod)
    // TODO: remove also all declarations reachable from all reachable non-abstract fake overrides in classes
    bridgesToGenerate.removeAll(findAllReachableDeclarations(implementation, mapToMethod))

    val method = mapToMethod(implementation)
    return bridgesToGenerate.filter { it != method }.map { Bridge(it, method) }.toSet()
}

private fun <Function : FunctionHandle, Method> findAllReachableDeclarations(function: Function, mapToMethod: (Function) -> Method): MutableSet<Method> {
    val collector = object : DFS.NodeHandlerWithListResult<Function, Method>() {
        override fun afterChildren(current: Function?) {
            if (current != null && current.isDeclaration) {
                result.add(mapToMethod(current))
            }
        }
    }
    [suppress("UNCHECKED_CAST")]
    DFS.dfs(listOf(function), { it!!.getOverridden() as Iterable<Function> }, collector)
    return HashSet(collector.result())
}

/**
 * Given a concrete function of any kind, finds an implementation (a concrete declaration) of this function in the supertypes.
 * The implementation is guaranteed to exist because if it wouldn't, the given function would've been abstract
 */
private fun <Function : FunctionHandle> findNonAbstractDeclaration(function: Function): Function {
    require(!function.isAbstract, "Only concrete functions have implementations: $function")

    if (function.isDeclaration) return function

    [suppress("UNCHECKED_CAST")]
    val concreteParents = function.getOverridden().filter { !it.isAbstract } as List<Function>
    if (concreteParents.isEmpty()) error("Concrete function should have at least one concrete super-function: $function")

    if (concreteParents.size == 1) {
        // TODO: get rid of recursion here
        return findNonAbstractDeclaration(concreteParents[0])
    }

    // If there's more than one concrete immediate super-function, we should find among all super-functions such function that:
    // 1) it's a concrete declaration
    // 2) it's not reachable from any other declaration reachable from the given function
    // The compiler guarantees that there will be exactly one such function.
    // The following algorithm is used: first, we find all declarations reachable from the given function. Then for each such declaration
    // we remove from that result all declarations reachable from it. The result is now guaranteed to have exactly one concrete declaration
    // (and possibly some abstract declarations, which don't matter)

    val result = findAllReachableDeclarations(function) { it }
    val toRemove = HashSet<Function>()
    for (declaration in result) {
        val reachable = findAllReachableDeclarations(declaration) { it }
        reachable.remove(declaration)
        toRemove.addAll(reachable)
    }
    result.removeAll(toRemove)

    if (result.isEmpty()) error("Cyclic hierarchy is present in overridden descriptors for function: $function")

    val concreteDeclarationSet = result.filter { !it.isAbstract }
    if (concreteDeclarationSet.size != 1) {
        error("Concrete fake override $function should have exactly one concrete super-declaration, but was $concreteDeclarationSet")
    }

    return concreteDeclarationSet[0]
}
