<template>
  <div id=selectCourseDef>
    <h4>Auswählbare Kurse aktualisieren</h4>
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
      <button :disabled="isUpdateCourseDefButtonDisabled" v-on:click="updateCourseDefsAndRefresh()">Aquabasilea Kurse
        aktualisieren
      </button>
    </div>
  </div>
</template>

<script>
import CourseDefApi from "@/mixins/CourseDefApi";
import Multiselect from '@vueform/multiselect';

export default {
  name: 'UpdateCourseDef',
  components: [Multiselect],
  mixins: [CourseDefApi],
  data() {
    return {
      selectedCourseDefLocation: [],
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
      return this.$store.getters.courseLocationsDtos
    },
  }
}
</script>