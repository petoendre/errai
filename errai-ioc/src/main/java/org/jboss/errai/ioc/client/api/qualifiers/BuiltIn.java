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

package org.jboss.errai.ioc.client.api.qualifiers;

import java.lang.annotation.Annotation;

/**
 * @author Mike Brock
 */
public final class BuiltIn {
  private BuiltIn() {
  }
  
  public static final Any ANY_INSTANCE = new Any() {
    @Override
    public Class<? extends Annotation> annotationType() {
      return Any.class;
    }
  };
  
  public static final Annotation[] DEFAULT_ANY_QUALIFIERS = new Annotation[] { ANY_INSTANCE };
}
