/*
 * Copyright 2011 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.codegen.meta;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public abstract class MetaClass implements HasAnnotations, MetaType, MetaGenericDeclaration {
  public abstract String getName();

  public abstract String getFullyQualifiedName();

  public abstract String getFullyQualifiedNameWithTypeParms();

  public abstract String getCanonicalName();

  public abstract String getInternalName();

  public abstract String getPackageName();

  public abstract MetaMethod[] getMethods();

  /**
   * Returns all declared and inherited methods on this class that have the
   * given annotation targeting them.
   * <p>
   * TODO: the returned collection should not include overridden methods from superclasses.
   *
   * @param annotation
   *          The annotation to scan this class's methods for. Must not be null.
   * @return An unmodifiable list of all declared and inherited methods of this
   *         class that are annotated with the given annotation.
   * @throws NullPointerException
   *           if {@code} annotation is null.
   */
  public abstract List<MetaMethod> getMethodsAnnotatedWith(Class<? extends Annotation> annotation);

  public abstract MetaMethod[] getDeclaredMethods();

  public abstract MetaMethod getMethod(String name, Class... parameters);

  public abstract MetaMethod getMethod(String name, MetaClass... parameters);

  public abstract MetaMethod getBestMatchingMethod(String name, Class... parameters);

  public abstract MetaMethod getBestMatchingMethod(String name, MetaClass... parameters);

  public abstract MetaMethod getBestMatchingStaticMethod(String name, Class... parameters);

  public abstract MetaMethod getBestMatchingStaticMethod(String name, MetaClass... parameters);

  public abstract MetaMethod getDeclaredMethod(String name, Class... parameters);

  public abstract MetaMethod getDeclaredMethod(String name, MetaClass... parameters);

  public abstract MetaField[] getFields();

  public abstract MetaField[] getDeclaredFields();

  public abstract MetaField getField(String name);

  public abstract MetaField getDeclaredField(String name);

  public abstract MetaConstructor[] getConstructors();

  public abstract MetaConstructor[] getDeclaredConstructors();

  public abstract MetaConstructor getConstructor(Class... parameters);

  public abstract MetaConstructor getConstructor(MetaClass... parameters);

  public abstract MetaConstructor getBestMatchingConstructor(Class... parameters);

  public abstract MetaConstructor getBestMatchingConstructor(MetaClass... parameters);

  public abstract MetaConstructor getDeclaredConstructor(Class... parameters);

  public abstract MetaParameterizedType getParameterizedType();

  public abstract MetaParameterizedType getGenericSuperClass();

  public abstract MetaClass[] getInterfaces();

  public abstract MetaClass getSuperClass();

  public abstract MetaClass getComponentType();

  public abstract MetaClass getOuterComponentType();

  /**
   * Reports if the type represented by this MetaClass is a supertype of (or the
   * same class as) the type represented by the given MetaClass. In other words,
   * this method returns true if the following code would compile without error,
   * where ThisType is the class represented by this MetaClass object, and
   * GivenType is the class represented by the given "clazz" argument:
   *
   * <pre>
   *   GivenType given = ...;
   *   ThisType a = given;
   * </pre>
   *
   * @param clazz
   *          The type to check for assignability to this MetaClass's type.
   * @return True if the given type is assignable to this metaclass's type.
   */
  public abstract boolean isAssignableFrom(MetaClass clazz);

  public abstract boolean isAssignableTo(MetaClass clazz);

  /**
   * Reports if the type represented by this MetaClass is a supertype of (or the
   * same class as) the given class. In other words, this method returns true if
   * the following code would compile without error, where ThisType is the class
   * represented by this MetaClass object, and GivenType is the class
   * represented by the given "clazz" argument:
   *
   * <pre>
   *   GivenType given = ...;
   *   ThisType a = given;
   * </pre>
   *
   * @param clazz The type to check for assignability to this MetaClass's type.
   * @return True if the given type is assignable to this metaclass's type.
   */
  public abstract boolean isAssignableFrom(Class clazz);

  public abstract boolean isAssignableTo(Class clazz);

  public abstract boolean isPrimitive();

  public abstract boolean isInterface();

  public abstract boolean isAbstract();

  public abstract boolean isArray();

  public abstract boolean isEnum();

  public abstract boolean isAnnotation();

  public abstract boolean isPublic();

  public abstract boolean isPrivate();

  public abstract boolean isProtected();

  public abstract boolean isFinal();

  public abstract boolean isStatic();

  public abstract boolean isVoid();

  public abstract boolean isDefaultInstantiable();

  public abstract MetaClass asBoxed();

  public abstract MetaClass asUnboxed();

  public abstract MetaClass asArrayOf(int dimensions);

  public abstract MetaClass getErased();

  public abstract boolean isPrimitiveWrapper();

  public abstract Class<?> asClass();
}
