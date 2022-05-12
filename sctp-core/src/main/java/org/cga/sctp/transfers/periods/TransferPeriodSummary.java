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

package org.cga.sctp.transfers.periods;

public interface TransferPeriodSummary {

    TransferPeriod transferPeriod();

    Long totalbeneficiaries();

    Long totalbeneficiariestotransfer();

    Long totalbeneficiariesfirsttransfer();

    Long totalbeneficiariesreceivetransfer();

    Long totalamountterm();

    Long totalamountarrears();

    Long totalamountfirsttransfer();

    Long totalamountarrearsuncollected();

    Long totalamountarrearsuntransferred();

    Long totalamountarrearsupdated();

    Long totalamountarrearsenrolment();

    Long totalamounttransfer();

    Long totalmembers();

    Long totalmembersprimary();

    Long totalmemberssecondary();

    Long numbermonthsliquidated();

    Long datetermsliquidated();

    Long datereconciliationclose();

    Long datecreation();

    Long usecreation_id();

    Long totalHH_totransfer();

    Long totalvaluesubsidy_totransfer();

    Long totalamountterm_totransfer();

    Long totalamountarrearsuncollected_totransfer();

    Long totalamountarrearsuntransferred_totransfer();

    Long totalamountarrearsupdated_totransfer();

    Long totalamounttransfer_totransfer();

    Long totalHH_nototransfer();

    Long totalvaluesubsidy_nototransfer();

    Long totalamountterm_nototransfer();

    Long totalamountarrearsuncollected_nototransfer();

    Long totalamountarrearsuntransferred_nototransfer();

    Long totalamountarrearsupdated_nototransfer();

    Long totalamounttransfer_nototransfer();

    Long totalmembersprimaryincentive();

    Long transferclose_use_id();

    Long personauthorizedclosetransfer();

    Long totalamountarrearsother();

    Long totalamount_HHoutgoing();

    Long totalamount_HHincoming();

    Long totalamount_OtherHHoutgoing();

    Long totalamount_OtherHHincoming();

    Long flagsystem();

    Long biodev_id();

    Long biodev_serial();

    Long startdateuploadreconciliation();

    Long enddateuploadreconciliation();

    Long totalvc();

    Long totalvc_startdateuploadreconciliation();

    Long totalta();

    Long totalHH_receivecollected();

    Long totalvaluesubsidy_receivecollected();

    Long totalamountterm_receivecollected();

    Long totalamountarrearsuncollected_receivecollected();

    Long totalamountarrearsuntransferred_receivecollected();

    Long totalamountarrearsupdated_receivecollected();

    Long totalamounttransfer_receivecollected();

    Long totalmembers_receivecollected();

    Long totalmembersprimary_receivecollected();

    Long totalmemberssecondary_receivecollected();

    Long totalmembersprimaryincentive_receivecollected();

    Long totalfirsttransfer_receivecollected();

    Long totalvc_receivecollected();

    Long totalHH_receivenocollected();

    Long totalamounttransfer_receivenocollected();

    Long totalvc_receivenocollected();

    Long totalHH_receive();

    Long totalvaluesubsidy_receive();

    Long totalamountterm_receive();

    Long totalamountarrearsuncollected_receive();

    Long totalamountarrearsuntransferred_receive();

    Long totalamountarrearsupdated_receive();

    Long totalamounttransfer_receive();

    Long totalmembers_receive();

    Long totalmembersprimary_receive();

    Long totalmemberssecondary_receive();

    Long totalmembersprimaryincentive_receive();

    Long totalfirsttransfer_receive();

    Long totaltopup();

    Long totaltopup_totransfer();

    Long totaltopup_nototransfer();

    Long totalamountarrearstopup();

    Long totalamountarrearstopup_discounted();

    Long totalamountarrearstopup_nondiscounted();

    Long totalamount_topupchangedistrict();

    Long totalnonrecertified();
}
