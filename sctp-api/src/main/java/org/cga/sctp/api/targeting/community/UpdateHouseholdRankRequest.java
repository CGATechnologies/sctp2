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

package org.cga.sctp.api.targeting.community;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public final class UpdateHouseholdRankRequest {
    @NotEmpty
    private List<UpdateHouseholdRankEntry> updates;

    public List<UpdateHouseholdRankEntry> getUpdates() {
        return updates;
    }

    public void setUpdates(List<UpdateHouseholdRankEntry> updates) {
        this.updates = updates;
    }

    public static class UpdateHouseholdRankEntry {
        @NotNull(message = "householdId must be specified")
        private Long householdId;
        @NotNull(message = "rank must be specified")
        private Long rank;
        @NotNull(message = "CbtStatus must be specified")
        private String cbtStatus;

        public Long getHouseholdId() {
            return householdId;
        }

        public void setHouseholdId(Long householdId) {
            this.householdId = householdId;
        }

        public Long getRank() {
            return rank;
        }

        public void setRank(Long rank) {
            this.rank = rank;
        }

        public String getCbtStatus() {
            return cbtStatus;
        }

        public void setCbtStatus(String cbtStatus) {
            this.cbtStatus = cbtStatus;
        }
    }
}
