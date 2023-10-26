<template>
  <div id=selectCourseDef class="select-course-def-container">
    <h5>Auswählbare Migros-Kurse aktualisieren</h5>
    <div class="grid-container-40-60">
      <label>Gewünschte Kursorte wählen</label>
      <select
          id="courseDefLocationSelector"
          name="courseDefLocation"
          class="select-course-location"
          multiple
          v-model="selectedCourseDefLocation">
        <option
            type="checkbox"
            v-for="courseLocationDto in courseLocationsDtos" :key="courseLocationDto"
            v-bind:value="courseLocationDto"> {{ courseLocationDto.courseLocationName }}
        </option>
      </select>
      <div>
        <label v-show="isCourseDefUpdateRunning">
          Aktualisierung der Kurse läuft...
        </label>
      </div>
      <CButton color="info" :disabled="isUpdateCourseDefButtonDisabled" v-on:click="evalSelectedCourseDefKeysUpdateCourseDefsAndRefresh()">
        Migros Kurse
        aktualisieren
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
      const selectedCourseDefLocationKeys = this.selectedCourseDefLocation.map(courseLocationDto => courseLocationDto.courseLocationKey);
      this.isCourseDefUpdateRunning = true;
      this.updateCourseDefsAndRefresh(JSON.stringify(selectedCourseDefLocationKeys),
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