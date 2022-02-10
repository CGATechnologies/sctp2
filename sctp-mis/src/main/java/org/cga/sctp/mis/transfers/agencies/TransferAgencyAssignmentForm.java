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

package org.cga.sctp.mis.transfers.agencies;

import org.cga.sctp.beneficiaries.Household;
import org.cga.sctp.location.Location;
import org.cga.sctp.program.Program;
import org.cga.sctp.transfers.agencies.TransferAgency;
import org.cga.sctp.transfers.agencies.TransferMethod;

/**
 * Links enrolled {@link Household}s with a {@link TransferAgency} within a specific {@link Program}
 * via the {@link Location}s which those households are registered under.
 */
public class TransferAgencyAssignmentForm {
    private Long id;
    private Long programId;
    private Long enrollmentSessionId;
    private Long locationId;
    private Long transferAgencyId;
    private Long assignedBy;
    private Long reviewedBy;
    private TransferMethod transferMethod;
    private Long amountPerHousehold; // TODO: REVIEW NECESSITY

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getEnrollmentSessionId() {
        return enrollmentSessionId;
    }

    public void setEnrollmentSessionId(Long enrollmentSessionId) {
        this.enrollmentSessionId = enrollmentSessionId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getTransferAgencyId() {
        return transferAgencyId;
    }

    public void setTransferAgencyId(Long transferAgencyId) {
        this.transferAgencyId = transferAgencyId;
    }

    public Long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Long getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Long reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public TransferMethod getTransferMethod() {
        return transferMethod;
    }

    public void setTransferMethod(TransferMethod transferMethod) {
        this.transferMethod = transferMethod;
    }

    public Long getAmountPerHousehold() {
        return amountPerHousehold;
    }

    public void setAmountPerHousehold(Long amountPerHousehold) {
        this.amountPerHousehold = amountPerHousehold;
    }
}
