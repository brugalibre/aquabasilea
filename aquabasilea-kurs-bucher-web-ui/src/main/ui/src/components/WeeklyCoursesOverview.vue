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
              {{ course.courseLocationDto.courseLocationName }}
            </label>
          </td>
          <td>
            <div style="display: flex">
              <button
                  v-c-tooltip="{content: getPauseButtonToolTipText(), placement: 'top'}"
                  class="pause-button table-button"
                  v-on:click="pauseCourseAndRefresh(course)">
              </button>
            </div>
          </td>
          <td>
            <div style="display: flex">
              <button
                  v-c-tooltip="{content: getDeleteButtonToolTipText(), placement: 'top'}"
                  class="delete-button table-button"
                  v-bind:class="{ 'table-button': !course.isPaused, 'inactive-table-button': course.isPaused}"
                  v-on:click="deleteCourseAndRefresh(course)">
              </button>
            </div>
          </td>
        </tr>
      </table>
    </div>
    <div class="weekly-courses-placeholder"></div>
    <a class="course-programm-link" href="https://aquabasilea.migrosfitnesscenter.ch/angebote/bewegung/kursprogramm"
       target="_blank">Offizielles Migrosfitness Kursprogramm</a>
  </div>
</template>
<script>
import WeeklyCoursesApi from '../mixins/WeeklyCoursesApi';
import CommonAquabasileaRestApi from '../mixins/CommonAquabasileaRestApi';

export default {
  name: 'WeeklyCoursesOverview',
  mixins: [WeeklyCoursesApi, CommonAquabasileaRestApi],
  computed: {
    courseBookingStateDto: function () {
      return this.$store.state.aquabasilea.courseBookingStateDto
    },
    weeklyCourses: function () {
      return this.$store.state.aquabasilea.weeklyCourses;
    },
  },
  methods: {
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
    hasToolTipText: function (course) {
      return this.getToolTipText(course) !== '';
    },
    deleteCourseAndRefresh: function (course) {
      this.$store.dispatch('aquabasilea/setIsLoading', true);
      this.deleteCourse(course);
      if (course.isCurrentCourse) {
        this.$emit('refreshCourseStateOverviewAndWeeklyCourses');
      } else {
        this.$emit('refreshWeeklyCourses');
      }
    },
    pauseCourseAndRefresh: function (course) {
      this.pauseResumeCourse(course);
      this.$emit('refreshCourseStateOverviewAndWeeklyCourses');
    },
  },
  mounted() {
    this.fetchWeeklyCourses();
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
  overflow-x: auto;
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