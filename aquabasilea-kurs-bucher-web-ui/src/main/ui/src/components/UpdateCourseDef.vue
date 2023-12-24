<template>
  <div id=selectCourseDef class="select-course-def-container">
    <h5>ACTIVE Fitness Center auswählen</h5>
    <div>
      <multiselect
          id="courseDefLocationSelector"
          name="courseDefLocation"
          class="select-course-location"
          :placeholder="'Kursort auswählen (tippen zum Suchen)'"
          :noResultsText="'Keine Ergebnisse'"
          :options="courseLocationsDtos"
          :label="'name'"
          :valueProp="'name'"
          :mode="'multiple'"
          v-model="selectedCourseDefLocation"
          :maxHeight=900
          :object=true
          :hideSelected="false"
          :searchable="true"
      >
        <template v-slot:option="{ option }">
          <span class="course-def-option">{{ option.name }}</span>
        </template>
      </multiselect>
      <div>
        <label v-show="isCourseDefUpdateRunning">
          Aktualisierung der Center läuft...
        </label>
      </div>
      <CButton color="info" :disabled="isUpdateCourseDefButtonDisabled" v-on:click="evalSelectedCourseDefKeysUpdateCourseDefsAndRefresh()" style="margin-top: 10px">
        Center aktualisieren
      </CButton>
    </div>
    <ErrorBox ref="errorBox"/>
  </div>
</template>

<script>

import CourseDefApi from "../mixins/CourseDefApi";
import ErrorBox from "@/components/error/ErrorBox.vue";
import ErrorHandlingService from "@/services/error/error-handling.service";

export default {
  name: 'UpdateCourseDef',
  mixins: [CourseDefApi],
  components: {
    ErrorBox
  },
  data() {
    return {
      selectedCourseDefLocation: [],
    }
  },
  watch: {
    courseLocationsDtos: {
      immediate: true,
      handler() {
        this.selectedCourseDefLocation = this.courseLocationsDtos.filter(courseLocation => courseLocation.isSelected);
      }
    }
  },
  methods: {
    evalSelectedCourseDefKeysUpdateCourseDefsAndRefresh: function () {
      const selectedCourseDefLocationCenterIds = this.selectedCourseDefLocation.map(courseLocationDto => courseLocationDto.centerId);
      this.isCourseDefUpdateRunning = true;
      this.updateCourseDefsAndRefresh(JSON.stringify(selectedCourseDefLocationCenterIds),
          error => ErrorHandlingService.handleError(this.$refs.errorBox, error),
          () => this.$emit('refreshAddCourse'));
    },
  },
  mounted() {
    this.fetchCourseLocations(error => ErrorHandlingService.handleError(this.$refs.errorBox, error));
    this.fetchIsCourseDefUpdateRunning(error => ErrorHandlingService.handleError(this.$refs.errorBox, error));
  },
  computed: {
    isUpdateCourseDefButtonDisabled: function () {
      return this.isCourseDefUpdateRunning || (!this.selectedCourseDefLocation || this.selectedCourseDefLocation.length === 0);
    },
    courseLocationsDtos: function () {
      return this.$store.state.aquabasilea.courseLocationsDtos
    },
  }
}

</script>