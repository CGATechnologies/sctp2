/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2022, CGATechnologies
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

package org.cga.sctp.transfers;

import org.cga.sctp.targeting.importation.parameters.UbrParameterValue;

import javax.persistence.AttributeConverter;

/**
 * Indicates status of a  Transfer event.
 */
public enum TransferStatus implements UbrParameterValue {
    /**
     * Transfer has not yet occurred i.e. cash has not been disbursed.
     */
    OPEN(19, "Open"),
    /**
     * Cash has been disbursed but Transfer is pending reconciliation and scrutiny
     */
    PRE_CLOSE(20, "Pre-Close"),
    /**
     * Cash disbursed and finances have been reconciled. Transfer cannot be re-opened.
     */
    CLOSED(21, "Close");

    private final int code;
    private final String name;

    TransferStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static TransferStatus valueOf(int code) {
        for (TransferStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Code " + code + " not found in " + TransferStatus.class.getCanonicalName());
    }

    @javax.persistence.Converter(autoApply = true)
    public static class Converter implements AttributeConverter<TransferStatus, Integer> {
        @Override
        public Integer convertToDatabaseColumn(TransferStatus attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.code;
        }

        @Override
        public TransferStatus convertToEntityAttribute(Integer dbData) {
            if (dbData == null) {
                return null;
            }
            return TransferStatus.valueOf(dbData);
        }
    }
}
