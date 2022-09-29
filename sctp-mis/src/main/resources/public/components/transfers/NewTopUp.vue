<!--
  ~ BSD 3-Clause License
  ~
  ~ Copyright (c) 2022, CGATechnologies
  ~ All rights reserved.
  ~
  ~ Redistribution and use in source and binary forms, with or without
  ~ modification, are permitted provided that the following conditions are met:
  ~
  ~ 1. Redistributions of source code must retain the above copyright notice, this
  ~    list of conditions and the following disclaimer.
  ~
  ~ 2. Redistributions in binary form must reproduce the above copyright notice,
  ~    this list of conditions and the following disclaimer in the documentation
  ~    and/or other materials provided with the distribution.
  ~
  ~ 3. Neither the name of the copyright holder nor the names of its
  ~    contributors may be used to endorse or promote products derived from
  ~    this software without specific prior written permission.
  ~
  ~ THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  ~ AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  ~ IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  ~ DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
  ~ FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
  ~ DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
  ~ SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
  ~ CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  ~ OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  ~ OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  -->
<template>
<div class="container">
    <!-- start content -->
    <div class="card card-default">
        <div class="card-header">
            <div class="card-header-title">New Top Up</div>
        </div>
        <div class="card-content">
            <form @submit.prevent="submit" action="" method="post" enctype="application/x-www-form-urlencoded">
                <input name="_csrf" value="3848477a-77f6-4ed2-ba7d-3ef5eaa3eb76" type="hidden">

                <div class="columns">
                    <div class="column is-half">
                        <div class="field">
                            <div class="is-normal">
                                <label class="label is-required">Top Up Name</label>
                            </div>
                            <div class="field-body">
                                <div class="field">
                                    <div class="control">
                                        <input id="name" name="name" required="required" type="text" autocomplete="off" value="" minlength="1"
                                               maxlength="100" class="input">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="column is-half">
                        <div class="field">
                            <div class="is-normal">
                                <label class="label is-required">Program</label>
                            </div>
                            <div class="field-body">
                                <div class="field">
                                    <div class="select is-fullwidth">
                                        <select id="programId" name="programId" v-model="topupForm.programId" class="input" required="required">
                                            <option disabled="disabled" selected="selected">Select Option</option>
                                            <option v-for="p in programs" :value="p.id" :key="p.id">{{p.name}}</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="columns">
                    <div class="column">

                    </div>
                    <div class="column">
                        <div class="field">
                            <div class="is-normal">
                                <label class="label is-required">Funder / Sponsor</label>
                            </div>
                            <div class="field-body">
                                <div class="field">
                                    <div class="select is-fullwidth">
                                        <select id="funderId" name="funderId" v-model="topupForm.funderId" class="input" required="required">
                                            <option disabled="disabled" selected="selected">Select Option</option>
                                            <option v-for="f in funders"  :value="f.id" :key="f.id">{{ f.name }}</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="columns">
                    <div class="column">
                        <div class="field">
                            <div class="is-normal">
                                <label class="label is-required">TopUp Types</label>
                            </div>
                            <div class="field-body">
                                <div class="field">
                                    <div class="select">
                                        <select id="topupType" name="topupType" class="input" required="required" v-model="topupForm.topupType">
                                            <option disabled="disabled" selected="selected">Select Option</option>
                                            <option value="FIXED_AMOUNT">Fixed Amount</option>
                                            <option value="PERCENTAGE_OF_RECIPIENT_AMOUNT">% of Recipient Amount</option>
                                            <option value="EQUIVALENT_BENEFICIARY_AMOUNT">Current HH monthly amount</option>
                                            <option value="EPAYMENT_ADMIN_FEE_TOPUP">E-Payment admin/cashout fee</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>


                    </div>

                    <div class="column">
                        <div class="optional-collapsible field">

                            <div class="field" v-if="isFixedTopup">
                                <div class="is-normal">
                                    <label class="label ">Fixed Amount Value</label>
                                </div>
                                <div class="field-body">
                                    <div class="field">
                                        <div class="control">
                                            <input id="percentage"
                                                   name="fixedAmount"
                                                   type="number"
                                                   v-model="topupForm.fixedAmount"
                                                   autocomplete="off" class="input">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="field" v-if="isPercentageTopup">
                                <div class="is-normal">
                                    <label class="label ">Percentage</label>
                                </div>
                                <div class="field-body">
                                    <div class="field">
                                        <div class="control">
                                            <input id="percentage"
                                                   name="percentage"
                                                   type="text"
                                                   v-model="topupForm.percentage"
                                                   autocomplete="off" value="" minlength="0"
                                                   maxlength="3" class="input">
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>

                <div class="field">
                    <div class="is-normal">
                        <label class="label is-required">Household Status</label>
                    </div>
                    <div class="field-body">
                        <div class="select">
                            <div class="select is-fullwidth">
                                <select id="householdStatus" name="householdStatus" v-model="topupForm.householdStatus" class="input" required="required">
                                    <option disabled="disabled" selected="selected">Select Option</option>
                                    <option value="BOTH">Both</option>
                                    <option value="RECERTIFIED">Recertified</option>
                                    <option value="NON_RECERTIFIED">Non-Recertified</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="geolocation-level columns">
                    <div class="field column">
                        <div class="is-normal">
                            <label class="label is-required">Location Type</label>
                        </div>
                        <div class="field-body">
                            <div class="field">
                                <div class="select is-fullwidth">
                                    <select id="locationType" name="locationType" v-model="topupForm.locationType" class="input" required="required">
                                        <option disabled="disabled" selected="selected">Select Option</option>
                                        <option value="SUBNATIONAL1">District</option>
                                        <option value="SUBNATIONAL3">T / A</option>
                                        <option value="SUBNATIONAL2">Village Cluster</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="field column">
                        <div class="is-normal">
                            <label class="label is-required">Location</label>
                        </div>
                        <div class="field-body">
                            <div class="field">
                                <div class="select is-fullwidth">
                                    <select id="locationId" name="locationId" class="input" required="required">
                                        <option disabled="disabled" selected="selected">Select Option</option>
                                        <option v-for="l in locations" :value="l.id" :key="l.id">{{ l.name }}</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="categorical-topup-elements">
                    <div class="option-wrapper">
                        <div class="checkbox-selector">
                            <label for="isCategorical">
                                <input type="checkbox" name="isCategorical" id="isCategorical"
                                       v-model="topupForm.isCategoricalTopUp"> Categorical TopUp?</label>
                        </div>
                        <div class="box-wrapper">
                            <div class="create-categorical-topups box" v-if="topupForm.isCategoricalTopUp">
                                <div class="field level-of-calculation">
                                    <label for="levelOfCalculation" class="label">Level of Calculation</label>
                                    <div class="select">
                                        <select name="levelOfCalculation" id="levelOfCalculation" class="control">
                                            <option value="">By Household</option>
                                            <option value="">By Individual</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="field age-range">
                                    <h5>Age Range</h5>
                                    <div class="age-range">
                                        <div class="field is-pulled-left">
                                            <label class="label" for="fromAge">From</label>
                                            <div class="select">
                                                <select id="" class="control">
                                                    <option value="" selected>Age From...</option>
                                                    <option v-for="age in range(0, 99)" :key="age" value="age">{{age}}</option>
                                                </select>
                                            </div>
                                        </div>

                                        <div class="field px-5 is-pulled-left">
                                            <label class="label">To</label>
                                            <div class="select">
                                                <select id="" class="control">
                                                    <option value="" selected>Age To ...</option>
                                                    <option v-for="age in range(0, 99)" :key="age" value="age">{{age}}</option>
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="is-clearfix"></div>
                                <div class="field">
                                    <label class="label" for="gender">Gender</label>
                                    <div class="select control">
                                        <select name="gender" id="">
                                            <option value="male">Both</option>
                                            <option value="male">Male</option>
                                            <option value="female">Female</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="field">
                                    <label class="label" for="gender">Disability</label>
                                    <div class="columns">
                                        <div for="" class="column" v-for="d in disabilityOptions" :key="d.id">
                                            <label for="" class="checkbox-label">
                                                <input type="checkbox" value="d.id" id=""> {{d.description}}
                                            </label>
                                        </div>
                                    </div>
                                </div>

                                <div class="field">
                                    <label for="gender">Chronic Illness</label>
                                    <div class="columns is-full">
                                        <div class="column" v-for="d in chronicIllenssOptions" :key="d.id">
                                            <label for="" class="checkbox-label">
                                                <input type="checkbox" value="d.id" id=""> {{d.description}}
                                            </label>
                                        </div>
                                    </div>
                                </div>

                                <div class="field">
                                    <label for="gender">Orphan Hood</label>
                                    <div class="columns is-gapless is-full">
                                        <div for="" class="column" v-for="d in orphanhoodOptions" :key="d.id">
                                            <label for="" class="checkbox-label">
                                                <input type="checkbox" value="d.id" id=""> {{d.description}}
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>
                        <div class="column"></div>
                    </div>

                </div>

                <div class="field">
                    <div class="is-normal">
                        <label class="label is-required">Active</label>
                    </div>
                    <div class="field-body">
                        <div class="field">
                            <div class="select">
                                <select id="active" name="active" class="input" required="required">
                                    <option disabled="disabled" selected="selected">Select Option</option>
                                    <option value="Yes">Yes</option>
                                    <option value="No">No</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="apply-period block">
                    <div class="field">
                        <label for="" class="checkbox-">
                            <input type="checkbox" v-model="topupForm.applyNextPeriod">&nbsp;Apply to Next Transfer Period
                        </label>
                    </div>
                    <p class="notification is-info" v-show="topupForm.applyNextPeriod">
                        Please note that created Topups will affect subsequent transfer calculations.
                    </p>
                </div>

                <div class="action-buttons">
                    <button class="button is-default">Preview Top Up Calculation</button>&nbsp;
                    <button class="button is-success">Create TopUp</button>
                </div>
            </form>
        </div>
    </div>
</div>
</template>
<script>
module.exports = {
    data() {
      return {
        funders: window.__pageData.funders || [],
        programs: window.__pageData.programs || [],
        topupForm: {
          name: '',
          funderId: '',
          programId: '',
          locationId: '',
          locationType: '',
          percentage: '',
          topupType: '',
          householdStatus: '',
          active: '',
          amount: '',
          topupAmount: 0.0,
          isCategoricalTopUp: false,
          applyNextPeriod: false,
          categorical: '',
          categoricalTargetingCriteriaId: '',
          discountedFromFunds: '',
          userId: '',
          categoricalTargetingLevel: '',
          ageFrom: '',
          ageTo: '',
          chronicIllnesses: '',
          orphanhoodStatuses: '',
          disabilities: '',
        },
        disabilityOptions: [
          { id: '1', description: 'Blind' },
          { id: '2', description: 'Deaf' },
          { id: '3', description: 'Speech impairment 		' },
          { id: '4', description: 'Deformed limbs' },
          { id: '5', description: 'Mentally disabled ' }
        ],
        chronicIllnessOptions: [
          { id: 1, description: 'Chronic malaria', category: 'Chronic Illness' },
          { id: 2, description: 'TB', category: 'Chronic Illness' },
          { id: 3, description: 'HIV / AIDS 		', category: 'Chronic Illness' },
          { id: 4, description: 'Asthma 			', category: 'Chronic Illness' },
          { id: 5, description: 'Arthritis 			', category: 'Chronic Illness' },
          { id: 6, description: 'Epilepsy 		', category: 'Chronic Illness' },
        ],
        orphanhoodOptions: [
          { id: 1, description: 'Single orphan' },
          { id: 2, description: 'Double orphan' },
          { id: 3, description: 'Not orphan' }
        ]
      }
    },
    computed: {
      isFixedTopup() {
        return this.topupForm.topupType === 'FIXED_AMOUNT';
      },

      isPercentageTopup() {
        return this.topupForm.topupType === 'PERCENTAGE_OF_RECIPIENT_AMOUNT';
      }
    },

    methods: {
      submit(event) {
        const url = "/transfers/topups/new";
        event.preventDefault()
        const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'] } };

        axios.post(url, this.topupForm, config)
          .then(response => {
            if (response.data) {
                alert("Top Up saved successfully.")
            }
          })
          .catch(err => {

          })
        return false
      },

      range(start, stop, step = 1) {
        return Array.from({ length: (stop - start) / step + 1}, (_, i) => start + (i * step));
      }
    }
}
</script>