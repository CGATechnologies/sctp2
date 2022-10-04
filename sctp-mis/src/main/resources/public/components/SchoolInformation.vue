<template>
  <section>
    <div class="content-tab">
      <b-message type="is-info">
        Add or edit information about school going children under this
        household.
      </b-message>

      <section>
        <b-table
          :data="isEmpty ? [] : schoolsEnrolled"
          :bordered="isBordered"
          :striped="isStriped"
          :narrowed="isNarrowed"
          :hoverable="isHoverable"
          :loading="isLoading"
          :focusable="isFocusable"
          :mobile-cards="hasMobileCards"
        >
          <b-table-column
            field="nameOfChild"
            label="Name of child"
            v-slot="props"
          >
            {{ props.row.individualName }}
          </b-table-column>

          <b-table-column
            field="HH Member Code"
            label="HH Member Code"
            v-slot="props"
          >
            {{ props.row.householdMemberCode }}
          </b-table-column>

          <b-table-column
            field="currentSchool"
            label="Current School"
            v-slot="props"
          >
            {{ props.row.schoolName }}
          </b-table-column>

          <b-table-column label="Education Level" v-slot="props">
            {{ props.row.highestEducationLevel }}
          </b-table-column>

          <b-table-column label="Grade" v-slot="props">
            <span> {{ props.row.gradeLevel }}</span>
          </b-table-column>

          <b-table-column label="Select School" v-slot="props">
            <span> Schools </span>
          </b-table-column>

          <template #empty>
            <div class="has-text-centered">No records to display</div>
          </template>
        </b-table>
      </section>
      <div class="divider is-horizontal"></div>
      <div class="columns">
        <div class="column">
          <div class="buttons">
            <b-button
              label="New School Enrollment"
              type="is-info"
              @click="isModalActive = true"
            />
          </div>
        </div>
        <div class="column">
          <div class="buttons is-right">
            <b-button type="is-info" @click="getSchoolsEnrolled">
              Reload
            </b-button>
            <!-- <b-button type="is-primary" @click="saveSchoolChildren">
              Save
            </b-button> -->
          </div>
        </div>
      </div>
    </div>
    <!-- 
    <School-Modal
      :is-active="isSchoolModalActive"
      :household-id="householdId"
      :session-id="sessionId"
    /> -->
    <section>
      <b-modal
        v-model="isModalActive"
        scroll="keep"
        @hidden="onHidden"
        has-modal-card
        trap-focus
        :destroy-on-hide="false"
        aria-role="dialog"
        aria-label="New School Enrollment"
        close-button-aria-label="Close"
      >
        <div class="card">
          <header class="modal-card-head">
            <p class="modal-card-title">New School Enrollment</p>
            <button
              type="button"
              class="delete"
              @click="isModalActive = false"
            />
          </header>
          <div class="card-content">
            <b-message type="is-info">
              Add information about a school going child under this household.
            </b-message>
            <section>
              <b-field label="Household Member">
                <Members-Dropdown :household-id="householdId" />
              </b-field>
              <b-field label="School">
                <b-select
                  name="schooldId"
                  placeholder="Select School"
                  required
                  expanded
                  v-model="schoolId"
                >
                  <option v-for="school in allSchools" :value="school.id">
                    {{ school.name }} - {{ school.code }} -
                    {{ school.educationZone }}
                  </option>
                </b-select>
              </b-field>
              <div class="columns">
                <div class="column">
                  <b-field label="Education Level">
                    <b-select
                      name="educationLevel"
                      placeholder="Select Education Level"
                      required
                      expanded
                      v-model="educationLevel"
                    >
                      <option
                        v-for="(level, index) in educationLevels"
                        :value="index + 1"
                      >
                        {{ level }}
                      </option>
                    </b-select>
                  </b-field>
                </div>
                <div class="column">
                  <b-field label="Grade Level">
                    <b-select
                      name="gradeLevel"
                      placeholder="Select Grade Level"
                      required
                      expanded
                      v-model="gradeLevel"
                    >
                      <option
                        v-for="(level, index) in gradeLevels"
                        :value="index + 1"
                      >
                        {{ level }}
                      </option>
                    </b-select>
                  </b-field>
                </div>
              </div>
              <div class="column">
                <b-field label="Is the child still active?">
                  <b-switch
                    v-model="status"
                    true-value="1"
                    false-value="0"
                  ></b-switch>
                </b-field>
              </div>
            </section>
          </div>
          <div class="modal-card-foot">
            <button class="button" type="button" @click="isModalActive = false">
              Close
            </button>
            <button
              class="button is-success"
              type="button"
              @click="saveSchoolInfo"
            >
              Save
            </button>
          </div>
        </div>
      </b-modal>
    </section>
  </section>
</template>

<script>
module.exports = {
  props: {
    householdId: {
      type: Number,
      required: true,
    },
    sessionId: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      children: [],
      schoolsEnrolled: [],
      data: [],
      isEmpty: false,
      isBordered: false,
      isStriped: true,
      isNarrowed: true,
      isHoverable: true,
      isFocusable: true,
      isLoading: false,
      hasMobileCards: true,
      isModalActive: false,
      children: [],
      allSchools: [],
      educationLevels: [],
      gradeLevels: [],
      status: 0,
      schoolId: null,
      educationLevel: null,
      gradeLevel: null,
      isModalActive: false,
    };
  },
  components: {
    MembersDropdown: httpVueLoader("/components/MembersDropdown.vue"),
  },
  mounted() {
    this.getSchoolsEnrolled();
    this.getAllSchools();
    this.getEducationLevels();
    this.getGradeLevels();
  },
  computed: {
    getModalStaus: function () {
      return this.isModalActive;
    },
  },
  methods: {
    snackbar(msg, msgType = "info") {
      this.$buefy.toast.open({
        duration: 5000,
        message: msg,
        position: "is-bottom",
        type: "is-" + msgType,
      });
    },
    errorDialog(msg, titleText = "Error") {
      this.$buefy.dialog.alert({
        title: titleText,
        message: msg,
        type: "is-danger",
        hasIcon: true,
        icon: "times-circle",
        iconPack: "fa",
        ariaRole: "alertdialog",
        ariaModal: true,
      });
    },
    getSchoolsEnrolled() {
      let vm = this;
      vm.isLoading = true;
      const params = [`household=${vm.householdId}`].join("&");
      axios
        .get(`/targeting/enrolment/schools-enrolled?${params}`)
        .then(function (response) {
          if (response.status == 200) {
            vm.schoolsEnrolled = JSON.parse(JSON.stringify(response.data.schools));
            console.log("schools " + JSON.stringify(response.data.schools));
          } else {
            throw `Server returned: ${response.status}`;
          }
        })
        .catch(function (error) {
          vm.errorDialog(
            "There was an error loading school information. Please try again"
          );
          console.log(error);
        })
        .then(function () {
          vm.isLoading = false;
        });
    },
    saveSchoolInfo() {
      let vm = this;
      vm.isLoading = true;
      var memberId = document.querySelector("#memberId").value;
      fData = new FormData();
      fData.append("individualId", memberId);
      fData.append("householdId", vm.householdId);
      fData.append("sessionId", vm.sessionId);
      fData.append("schoolId", vm.schoolId);
      fData.append("educationLevel", vm.educationLevel);
      fData.append("grade", vm.gradeLevel);
      fData.append("status", vm.status);

      // Display the key/value pairs
      for (var pair of fData.entries()) {
        console.log(pair[0] + ", " + pair[1]);
      }

      const config = {
        headers: {
          "X-CSRF-TOKEN": csrf()["token"],
          "Content-Type": "multipart/form-data",
        },
      };
      axios
        .post(`/targeting/enrolment/update-school`, fData, config)
        .then(function (response) {
          if (response.status === 200) {
            vm.isModalActive = false;
            vm.msgDialog("Updated successfully.", "", "success", "check");
          } else {
            throw `Status: ${response.status}`;
          }
        })
        .catch(function (error) {
          console.log(error);
          vm.errorDialog("Error updating data");
        })
        .then(function () {
          vm.isLoading = false;
        });
    },
    onHidden() {
      console.log("School modal is hidden");
      // this.$emit("open-close-school-modal", false);
      this.$emit("isSchoolModalActive", false);
      // this.isActive = false;
    },
    getAllSchools() {
      let vm = this;
      vm.isLoading = true;
      axios
        .get(`/targeting/enrolment/schools`)
        .then(function (response) {
          if (response.status == 200) {
            vm.allSchools = response.data.schools;
          } else {
            throw `Server returned: ${response.status}`;
          }
        })
        .catch(function (error) {
          vm.errorDialog(
            "There was an error loading schools. Please try again"
          );
          console.log(error);
        })
        .then(function () {
          vm.isLoading = false;
        });
    },
    getEducationLevels() {
      let vm = this;
      vm.isLoading = true;
      axios
        .get(`/targeting/enrolment/education-levels`)
        .then(function (response) {
          if (response.status == 200) {
            vm.educationLevels = response.data.educationLevels;
          } else {
            throw `Server returned: ${response.status}`;
          }
        })
        .catch(function (error) {
          vm.errorDialog(
            "There was an error loading education levels. Please try again"
          );
          console.log(error);
        })
        .then(function () {
          vm.isLoading = false;
        });
    },
    getGradeLevels() {
      let vm = this;
      vm.isLoading = true;
      axios
        .get(`/targeting/enrolment/grade-levels`)
        .then(function (response) {
          if (response.status == 200) {
            vm.gradeLevels = response.data.gradeLevels;
          } else {
            throw `Server returned: ${response.status}`;
          }
        })
        .catch(function (error) {
          vm.errorDialog(
            "There was an error loading grade levels. Please try again"
          );
          console.log(error);
        })
        .then(function () {
          vm.isLoading = false;
        });
    },
  },
};
</script>