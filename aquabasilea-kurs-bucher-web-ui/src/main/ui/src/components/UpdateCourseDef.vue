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
      <CButton color="info" :disabled="isUpdateCourseDefButtonDisabled" v-on:click="updateCourseDefsAndRefresh()">
        Migros Kurse
        aktualisieren
      </CButton>
    </div>
  </div>
</template>

<script>

import CourseDefApi from "../mixins/CourseDefApi";

export default {
  name: 'UpdateCourseDef',
  mixins: [CourseDefApi],
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
    updateCourseDefsAndRefresh: function () {
      const selectedCourseDefLocationKeys = this.selectedCourseDefLocation.map(courseLocationDto => courseLocationDto.courseLocationKey);
      this.updateCourseDefs(JSON.stringify(selectedCourseDefLocationKeys));
      this.$emit('refreshAddCourse');
    },
  },
  mounted() {
    this.fetchCourseLocations();
    this.fetchIsCourseDefUpdateRunning();
  },
  computed: {
    isUpdateCourseDefButtonDisabled: function () {
      return this.isCourseDefUpdateRunning || (!this.selectedCourseDefLocation || this.selectedCourseDefLocation.length == 0);
    },
    courseLocationsDtos: function () {
      return this.$store.state.aquabasilea.courseLocationsDtos
    },
  }
}

</script>