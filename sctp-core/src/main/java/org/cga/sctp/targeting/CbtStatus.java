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

package org.cga.sctp.targeting;

import javax.persistence.AttributeConverter;

public enum CbtStatus {
    NonRecertified(4),
    PreEligible(6),
    Ineligible(2),
    Eligible(1),
    Selected(3),
    Enrolled(5);

    public final int code;
    public static final CbtStatus[] VALUES = values();

    CbtStatus(int code) {
        this.code = code;
    }

    public static CbtStatus valueOf(int code) {
        for (CbtStatus status : VALUES) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Code " + code + " not found in " + CbtStatus.class.getCanonicalName());
    }

    @javax.persistence.Converter(autoApply = true)
    public static class Converter implements AttributeConverter<CbtStatus, Integer> {

        @Override
        public Integer convertToDatabaseColumn(CbtStatus attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.code;
        }

        @Override
        public CbtStatus convertToEntityAttribute(Integer dbData) {
            if (dbData == null) {
                return null;
            }
            return CbtStatus.valueOf(dbData);
        }
    }
}
