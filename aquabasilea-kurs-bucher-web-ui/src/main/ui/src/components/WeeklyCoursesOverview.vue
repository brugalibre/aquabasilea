<template>
  <div class="weekly-courses-overview grid-container">
    <h2>Wöchentliche Kurse verwalten</h2>
    <div class="table">
      <table>
        <tr>
          <th id="courseName">Kurs Name</th>
          <th id="dayOfWeek">Wochentag</th>
          <th id="timeOfTheDay">Uhrzeit</th>
          <th id="courseLocation">Kurs Ort</th>
          <th id="pause">Kurs pausieren</th>
          <th id="delete">Kurs löschen</th>
        </tr>
        <tr v-for="course in weeklyCourses.courseDtos" :key="course.id"
            v-bind:class="{ isPaused: course.isPaused, isAppPaused: (this.courseBookingStateDto.state === 'PAUSED' && !course.isPaused),
             hasNoCourseDef: !course.hasCourseDef}"
        >
          <td class="table-cell">
            <div style="display: grid">
              <div
                  v-c-tooltip="{content: getToolTipText(course), placement: 'top', visible: course.tooltipText !== ''}">
                <span v-show="!course.hasCourseDef" class="no-course-def-warning cell-icon"></span>
                <span v-show="course.isCurrentCourse && this.courseBookingStateDto.state !== 'PAUSED'"
                      class="current-course-star cell-icon"/>
                <label>{{ course.courseName }}</label>
              </div>
            </div>
          </td>
          <td class="table-cell">
            <label
                v-c-tooltip="{content: formatDate(course.courseDate), placement: 'top'}"
            >
              {{ course.dayOfWeek }}
            </label>
          </td>
          <td class="table-cell">
            <label>{{ course.timeOfTheDay }}</label>
          </td>
          <td class="table-cell">
            <label>
              {{ course.courseLocationDto.name }}
            </label>
          </td>
          <td>
            <div style="display: flex">
              <button
                  :disabled="courseBookingStateDto.state === 'BOOKING'"
                  v-bind:class="{ 'table-button': courseBookingStateDto.state !== 'BOOKING', 'inactive-table-button': courseBookingStateDto.state === 'BOOKING'}"
                  v-c-tooltip="{content: getPauseButtonToolTipText(), placement: 'top'}"
                  class="pause-button table-button"
                  v-on:click="this.pauseResumeCourseAndRefresh(course, error => this.handleError(error),
                  () => this.refresh(true))">
              </button>
            </div>
          </td>
          <td>
            <div style="display: flex">
              <button
                  :disabled="courseBookingStateDto.state === 'BOOKING'"
                  v-c-tooltip="{content: getDeleteButtonToolTipText(), placement: 'top'}"
                  class="delete-button table-button"
                  v-bind:class="{ 'table-button': !course.isPaused, 'inactive-table-button': course.isPaused || courseBookingStateDto.state === 'BOOKING'}"
                  v-on:click="this.deleteCourseAndRefresh(course, error => this.handleError(error), () => this.refresh(course.isCurrentCourse))">
              </button>
            </div>
          </td>
        </tr>
      </table>
    </div>
    <div v-if="this.weeklyCourses.courseDtos.length > 0" class="weekly-courses-placeholder"/>
    <a class="course-programm-link" href="https://www.activfitness.ch/kursreservation/"
       target="_blank">Offizielles ACTIV Fitness Kursprogramm</a>
    <ErrorBox ref="errorBox"/>
    <div v-if="this.weeklyCourses.courseDtos.length === 0" class="weekly-courses-placeholder"/>
  </div>
</template>
<script>
import WeeklyCoursesApi from '../mixins/WeeklyCoursesApi';
import CommonAquabasileaRestApi from '../mixins/CommonAquabasileaRestApi';
import ErrorBox from "@/components/error/ErrorBox.vue";
import ErrorHandlingService from "@/services/error/error-handling.service";

export default {
  name: 'WeeklyCoursesOverview',
  mixins: [WeeklyCoursesApi, CommonAquabasileaRestApi],
  components: {
    ErrorBox
  },
  computed: {
    courseBookingStateDto: function () {
      return this.$store.state.aquabasilea.courseBookingStateDto
    },
    weeklyCourses: function () {
      return this.$store.state.aquabasilea.weeklyCourses;
    },
  },
  methods: {
    /*
    * In case of an error during fetching the courses -> reset the current courses since we don't
    * actual< know what values would be loaded
    * and set the error details
     */
    fetchWeeklyCoursesErrorCallbackHandler: function (error) {
      this.resetWeeklyCourseDtosAndStore();
      this.handleError(error);
    },
    handleError: function (error) {
      ErrorHandlingService.handleError(this.$refs.errorBox, error);
    },
    getToolTipText: function (course) {
      if (this.courseBookingStateDto.state === 'PAUSED') {
        return '';
      }
      return course.tooltipText;
    },
    getDeleteButtonToolTipText: function () {
      return 'Löscht diesen Kurs. Keine Angst, er kann ganz einfach wieder hinzugefügt werden';
    },
    getPauseButtonToolTipText: function () {
      return 'Pausiert diesen Kurs. Wenn es noch weitere, aktive Kurse gibt, wird dieser Kurs nur für eine Woche ausgesetzt.\n' +
          'Ist es hingegen der einzige Kurs, wird die gesamte Applikation pausiert';
    },
    refresh: function (refreshWeeklyCoursesAndCourseState) {
      if (refreshWeeklyCoursesAndCourseState) {
        this.$emit('refreshCourseStateOverviewAndWeeklyCourses');
      } else {
        this.$emit('refreshWeeklyCourses');
      }
    },
  },
  mounted() {
    this.fetchWeeklyCourses(error => this.fetchWeeklyCoursesErrorCallbackHandler(error));
  }
}</script>

<style scoped>

.weekly-courses-overview {
  overflow-y: hidden;
  display: flex;
  height: auto;
  justify-content: space-between;
  flex-direction: column;
}

.table {
  /*overflow-x: auto;*/
}

.table-cell {
  padding-left: 13px;
}

label {
  word-break: break-all;
}

.isPaused {
  background: lightslategray;
}

.isAppPaused {
  background: lightgrey;
}

.hasNoCourseDef {
  background: #ffcccb;
  border: firebrick solid 2px !important;
  border-radius: 5px;
}

.pause-button {
  background: url("../assets/pause.svg") transparent no-repeat center;
  background-size: 90% 90%;
}

.delete-button {
  background: url("../assets/trash.png") transparent no-repeat center;
  background-size: 90% 90%;
}

.table-button {
  margin: auto;
  height: 25px;
  width: 25px;
  cursor: pointer;
  border-radius: 5px;
  box-shadow: inset 0 3px 6px rgba(0, 0, 0, 0.16), 0 4px 6px rgba(0, 0, 0, 0.45);
}

.inactive-table-button {
  margin: auto;
  height: 25px;
  width: 25px;
  cursor: auto;
  border-radius: 5px;
  box-shadow: inset 0 3px 6px rgba(0, 0, 0, 0.16), 0 4px 6px rgba(0, 0, 0, 0.45);
}

.current-course-star {
  background: url('../assets/glocke.png') transparent no-repeat center;
}

.cell-icon {
  height: 15px;
  width: 15px;
  padding-right: 25px;
  background-size: 90% 90%;
  margin-right: 5px;
}

.no-course-def-warning {
  background: url('../assets/warning.svg') transparent no-repeat center;
}

.weekly-courses-placeholder {
  flex-grow: 2;
}

.course-programm-link {
  align-self: center;
}
</style>