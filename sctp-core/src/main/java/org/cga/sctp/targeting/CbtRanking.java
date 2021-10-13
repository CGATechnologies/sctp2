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

import javax.persistence.Convert;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Community based targeting record (from view)
 */
@SuppressWarnings("unused")
public interface CbtRanking {
    /*@Id
    private Long householdId;
    private Integer rank;
    private String taName;
    private String zoneName;
    private String formNumber;
    private Long cbtSessionId;
    private String clusterName;
    private String villageName;
    private BigDecimal pmtScore;
    private String districtName;
    private Boolean cbtSelection;*/

    /*@Convert(converter = CbtStatus.Converter.class)
    private CbtStatus status;*/

    public Long getHouseholdId();

    public String getTaName();

    public String getClusterName();

    public String getVillageName();

    public String getDistrictName();

    public String getHouseholdHead();

    public Integer getRank();

    public LocalDateTime getCreatedAt();

    public String getZoneName();

    public String getFormNumber();

    public BigDecimal getPmtScore();

    public Long getCbtSessionId();

    public Boolean getCbtSelection();

    Long getMemberCount();

    String getVillageHeadName();

    /**
     * Household ML code
     *
     * @return
     */
    Long getMlCode();

    @Convert(converter = CbtStatus.Converter.class)
    public CbtStatus getStatus();
}
