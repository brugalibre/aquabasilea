<template>
  <div class="weekly-courses-overview">
    <h2>Kurse verwalten</h2>
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
            v-bind:class="{ isPaused: course.isPaused, isAppPaused: (this.courseBookingStateDto.state === 'PAUSED' && !course.isPaused)}">
          <td class="table-cell">
              <span v-show="course.isCurrentCourse && this.courseBookingStateDto.state !== 'PAUSED'"
                    class="current-course-star"/>
            <label>{{ course.courseName }}</label>
          </td>
          <td class="table-cell">
            <label>
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
                  class="pause-button table-button"
                  v-on:click="pauseCourseAndRefresh(course)">
              </button>
            </div>
          </td>
          <td>
            <div style="display: flex">
              <button
                  class="delete-button"
                  :disabled="course.isPaused"
                  v-bind:class="{ 'table-button': !course.isPaused, 'inactive-table-button': course.isPaused}"
                  v-on:click="deleteCourseAndRefresh(course)">
              </button>
            </div>
          </td>
        </tr>
      </table>
    </div>
    <div class="placeholder"></div>
    <a class="course-programm-link" href="https://aquabasilea.migrosfitnesscenter.ch/angebote/bewegung/kursprogramm"
       target="_blank">Aquabasilea
      kursprogramm</a>
  </div>
</template>
<script>
import WeeklyCoursesApi from '../mixins/WeeklyCoursesApi';

export default {
  name: 'WeeklyCoursesOverview',
  mixins: [WeeklyCoursesApi],
  computed: {
    courseBookingStateDto: function () {
      return this.$store.getters.courseBookingStateDto
    },
    weeklyCourses: function () {
      return this.$store.getters.weeklyCourses;
    },
  },
  methods: {
    onCourseClick: function (course, setAttrEditableFunction) {
      if (!course.isPaused) {
        setAttrEditableFunction.apply(course);
      }
    },
    deleteCourseAndRefresh: function (course) {
      this.$store.dispatch('setIsLoading', true);
      this.deleteCourse(course);
      if (course.isCurrentCourse) {
        this.$emit('refreshCourseStateOverviewAndWeeklyCourses');
      } else {
        this.$emit('refreshWeeklyCourses');
      }
    },
    pauseCourseAndRefresh: function (course) {
      this.$store.dispatch('setIsLoading', true);
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
  padding-left: 8px;
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
  border-radius: 5px;
  box-shadow: inset 0 3px 6px rgba(0, 0, 0, 0.16), 0 4px 6px rgba(0, 0, 0, 0.45);
}

.current-course-star {
  background: url('../assets/glocke.png') transparent no-repeat center;
  height: 15px;
  width: 15px;
  padding-right: 25px;
  background-size: 90% 90%;
  margin-right: 5px;
}

.placeholder {
  flex-grow: 2;
}

.course-programm-link {
  align-self: center;
}
</style>