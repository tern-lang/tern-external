/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ternlang.dx.cf.iface;

import org.ternlang.dx.rop.cst.CstNat;
import org.ternlang.dx.rop.cst.CstString;
import org.ternlang.dx.rop.cst.CstType;

/*
 * Standard implementation of {@link Member}, which directly stores
 * all the associated data.
 */
public abstract class StdMember implements Member {
    /* {@code non-null;} the defining class */
    private final CstType definingClass;

    /* access flags */
    private final int accessFlags;

    /* {@code non-null;} member name and type */
    private final CstNat nat;

    /* {@code non-null;} list of associated attributes */
    private final AttributeList attributes;

    /*
     * Constructs an instance.
     *
     * @param definingClass {@code non-null;} the defining class
     * @param accessFlags access flags
     * @param nat {@code non-null;} member name and type (descriptor)
     * @param attributes {@code non-null;} list of associated attributes
     */
    public StdMember(CstType definingClass, int accessFlags, CstNat nat,
                     AttributeList attributes) {
        if (definingClass == null) {
            throw new NullPointerException("definingClass == null");
        }

        if (nat == null) {
            throw new NullPointerException("nat == null");
        }

        if (attributes == null) {
            throw new NullPointerException("attributes == null");
        }

        this.definingClass = definingClass;
        this.accessFlags = accessFlags;
        this.nat = nat;
        this.attributes = attributes;
    }

    /* {@inheritDoc} */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);

        sb.append(getClass().getName());
        sb.append('{');
        sb.append(nat.toHuman());
        sb.append('}');

        return sb.toString();
    }

    /* {@inheritDoc} */
    public final CstType getDefiningClass() {
        return definingClass;
    }

    /* {@inheritDoc} */
    public final int getAccessFlags() {
        return accessFlags;
    }

    /* {@inheritDoc} */
    public final CstNat getNat() {
        return nat;
    }

    /* {@inheritDoc} */
    public final CstString getName() {
        return nat.getName();
    }

    /* {@inheritDoc} */
    public final CstString getDescriptor() {
        return nat.getDescriptor();
    }

    /* {@inheritDoc} */
    public final AttributeList getAttributes() {
        return attributes;
    }
}