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

import com.fasterxml.jackson.annotation.JsonValue;

public enum HarvestedProduceDuration implements UbrParameterValue {
    LESS_THAN_THREE_MONTHS(1, "0 < 3 months"),
    THREE_TO_FIVE_MONTHS(2, "3 - 5 months"),
    SIX_MONTHS_AND_OVER(3, "6 months and over"),
    DID_NOT_HARVEST(4, "Did not harvest"),
    NOTHING_IN_STORE(4, "Nothing in store");

    public final int code;
    public final String title;
    public static final HarvestedProduceDuration[] VALUES = values();

    HarvestedProduceDuration(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public static HarvestedProduceDuration parseCode(String code) {
        return parseIntCode(Integer.parseInt(code));
    }

    public static HarvestedProduceDuration parseIntCode(int id) {
        for(HarvestedProduceDuration e: VALUES) {
            if (e.code == id) return e;
        }
        return null;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return title;
    }

    @JsonValue
    public String getValueLiteral() {
        return name();
    }
}
