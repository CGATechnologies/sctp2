/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, CGATechnologies
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.cga.sctp.targeting.importation.parameters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RelationshipToHead implements UbrParameterValue {
    // We can't rely on the intrinsic ordinal because the structure of the enum could be rearranged
    // So we define a code property
    Head(1, "Head"),
    Spouse(2, "Spouse"),
    OwnChild(3, "Own child"),
    BrotherOrSister(4, "Brother/Sister"),
    GrandChild(5, "Grandchild"),
    Parent(6, "Parent"),
    OtherRelative(7, "Other relative"),
    NotRelated(8, "Not related");

    public final int code;
    public static final RelationshipToHead[] VALUES = values();
    public final String description;

    RelationshipToHead(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static RelationshipToHead parseIntCode(int id) {
        for(RelationshipToHead e: VALUES) {
            if (e.code == id) return e;
        }
        return null;
    }

    public static RelationshipToHead parseCode(String code) {
        return parseIntCode(Integer.parseInt(code));
    }

    @Override
    public int getCode() {
        return code;
    }

    @JsonValue
    public String getValueLiteral() {
        return name();
    }
}
