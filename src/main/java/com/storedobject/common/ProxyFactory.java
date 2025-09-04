/*
 * Copyright (c) 2018-2025 Syam Pillai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.storedobject.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for creating proxies that implement multiple interfaces, handling classloader visibility.
 *
 * @author Syam
 */
public class ProxyFactory {

    /**
     * Creates a dynamic proxy instance that implements the specified interfaces and routes method calls
     * to the provided {@link InvocationHandler}.
     *
     * @param <T>           the type of the proxy being returned
     * @param handler       the {@link InvocationHandler} for handling method calls on the proxy instance
     * @param interfaceTypes the array of interface classes that the proxy instance will implement
     * @return a proxy instance that implements the specified interfaces and forwards method calls to the handler
     * @throws IllegalArgumentException if the handler is null, no interfaces are provided, or one of the specified
     *                                  classes is not an interface
     * @throws RuntimeException         if the proxy cannot be created due to classloader visibility issues
     */
    @SafeVarargs
    public static <T> T createProxy(InvocationHandler handler, Class<T>... interfaceTypes) {
        if (handler == null) {
            throw new IllegalArgumentException("InvocationHandler cannot be null");
        }
        if (interfaceTypes == null || interfaceTypes.length == 0) {
            throw new IllegalArgumentException("At least one interface type is required");
        }

        // Validate all types are interfaces
        for (Class<?> interfaceType : interfaceTypes) {
            if (!interfaceType.isInterface()) {
                throw new IllegalArgumentException("All types must be interfaces: " + interfaceType.getName());
            }
        }

        ClassLoader classLoader = determineAppropriateClassLoader(interfaceTypes);

        try {
            @SuppressWarnings("unchecked")
            T proxy = (T) Proxy.newProxyInstance(
                    classLoader,
                    interfaceTypes,
                    handler
            );
            return proxy;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("ClassLoader visibility issue for interfaces: " +
                    Arrays.toString(interfaceTypes), e);
        }
    }

    /**
     * Determines the most appropriate classloader for all interfaces
     */
    private static ClassLoader determineAppropriateClassLoader(Class<?>[] interfaceTypes) {
        // Strategy 1: Try to find a common classloader that can see all interfaces
        ClassLoader commonLoader = findCommonClassLoader(interfaceTypes);
        if (commonLoader != null) {
            return commonLoader;
        }

        // Strategy 2: Use the first interface's classloader (with null check)
        ClassLoader firstLoader = interfaceTypes[0].getClassLoader();
        if (firstLoader != null) {
            return firstLoader;
        }

        // Strategy 3: Use thread context classloader
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        if (contextLoader != null) {
            return contextLoader;
        }

        // Strategy 4: Fallback to system classloader
        return ClassLoader.getSystemClassLoader();
    }

    /**
     * Attempts to find a classloader that can load all interfaces
     */
    private static ClassLoader findCommonClassLoader(Class<?>[] interfaceTypes) {
        if (interfaceTypes.length == 1) {
            return interfaceTypes[0].getClassLoader();
        }

        // Collect all unique classloaders
        Set<ClassLoader> loaders = new HashSet<>();
        for (Class<?> interfaceType : interfaceTypes) {
            ClassLoader loader = interfaceType.getClassLoader();
            if (loader != null) {
                loaders.add(loader);
            }
        }

        // If all interfaces have the same classloader (or null)
        if (loaders.size() == 1) {
            return loaders.iterator().next();
        }

        // Try to find a parent classloader that can see all interfaces
        for (ClassLoader candidate : loaders) {
            if (canSeeAllInterfaces(candidate, interfaceTypes)) {
                return candidate;
            }
        }

        // No common classloader found
        return null;
    }

    /**
     * Checks if a classloader can load all the interface classes
     */
    private static boolean canSeeAllInterfaces(ClassLoader classLoader, Class<?>[] interfaceTypes) {
        try {
            for (Class<?> interfaceType : interfaceTypes) {
                // Try to load the class through the candidate classloader
                Class<?> loadedClass = Class.forName(interfaceType.getName(), false, classLoader);
                if (loadedClass != interfaceType) {
                    return false; // Different class instance means different classloader
                }
            }
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Creates a dynamic proxy instance with the specified {@link ClassLoader},
     * handler, and interfaces. The proxy instance will implement the specified
     * interfaces and route method calls to the provided {@link InvocationHandler}.
     *
     * @param <T>            the type of the proxy being returned
     * @param classLoader    the {@link ClassLoader} used to define the proxy class
     * @param handler        the {@link InvocationHandler} for intercepting method
     *                       calls on the proxy instance
     * @param interfaceTypes the array of interface classes that the proxy
     *                       instance will implement
     * @return a proxy instance that implements the specified interfaces and
     *         forwards method calls to the handler
     * @throws IllegalArgumentException if the classLoader is null, the handler
     *                                  is null, no interfaces are provided, or any
     *                                  of the specified classes is not an interface
     * @throws RuntimeException         if the proxy cannot be created due
     *                                  to classLoader visibility issues
     */
    @SafeVarargs
    public static <T> T createProxyWithClassLoader(ClassLoader classLoader,
                                                   InvocationHandler handler,
                                                   Class<T>... interfaceTypes) {
        if (classLoader == null) {
            throw new IllegalArgumentException("ClassLoader cannot be null");
        }

        // Validate interfaces
        for (Class<?> interfaceType : interfaceTypes) {
            if (!interfaceType.isInterface()) {
                throw new IllegalArgumentException("All types must be interfaces: " + interfaceType.getName());
            }

            // Verify the classloader can see this interface
            try {
                Class<?> loadedInterface = Class.forName(interfaceType.getName(), false, classLoader);
                if (loadedInterface != interfaceType) {
                    throw new IllegalArgumentException("ClassLoader cannot see interface: " +
                            interfaceType.getName());
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("ClassLoader cannot load interface: " +
                        interfaceType.getName(), e);
            }
        }

        @SuppressWarnings("unchecked")
        T proxy = (T) Proxy.newProxyInstance(
                classLoader,
                interfaceTypes,
                handler
        );
        return proxy;
    }
}