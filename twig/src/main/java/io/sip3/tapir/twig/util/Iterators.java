/*
 *    Copyright 2017 SIP3.IO CORP.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.sip3.tapir.twig.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by agafox.
 */
public class Iterators {

    public static <T> Iterator<T> merge(Iterator<? extends T> i1, Iterator<? extends T> i2, Comparator<T> c) {
        return new Iterator<T>() {

            private T v1 = i1 != null ? next(i1) : null;
            private T v2 = i2 != null ? next(i2) : null;

            @Override
            public boolean hasNext() {
                return v1 != null || v2 != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T v;
                if (v1 != null && (v2 == null || c.compare(v1, v2) <= 0)) {
                    v = v1;
                    v1 = next(i1);
                } else {
                    v = v2;
                    v2 = next(i2);
                }
                return v;
            }

            private T next(Iterator<? extends T> i) {
                return i.hasNext() ? i.next() : null;
            }
        };
    }

    public static <T> boolean compare(Iterator<T> i1, Iterator<T> i2, Comparator<T> c) {
        while (i1.hasNext() && i2.hasNext() && c.compare(i1.next(), i2.next()) == 0) {
            // Do nothing
        }
        return !i1.hasNext() && !i2.hasNext();
    }
}
