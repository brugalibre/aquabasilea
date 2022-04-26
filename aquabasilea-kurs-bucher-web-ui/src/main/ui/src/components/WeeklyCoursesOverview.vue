<template>
  <div class="weekly-courses-overview">
    <h2>Kurse verwalten</h2>
    <div class="table">
      <loading v-model:active="isLoading"
               :is-full-page="true"/>
      <table>
        <tr>
          <th id="courseName">Kurs Name</th>
          <th id="dayOfWeek">Wochentag</th>
          <th id="timeOfTheDay">Uhrzeit</th>
          <th id="pause">Kurs pausieren</th>
          <th id="delete">Kurs löschen</th>
        </tr>
        <tr v-for="course in weeklyCourses.courseDtos" :key="course.id"
            v-bind:class="{ isPaused: course.isPaused}">
          <td class="table-cell">
            <div v-show="!course.isCourseNameEditable">
              <span v-show="course.isCurrentCourse" class="current-course-star"/>
              <label
                  @click="onCourseClick(course, () => course.isCourseNameEditable = true)">
                {{ course.courseName }}
              </label>
            </div>
            <input v-show="course.isCourseNameEditable"
                   v-model="course.courseName"
                   name="courseName"
                   v-on:blur="course.isCourseNameEditable=false;"
                   @keyup.esc="course.isDurationRepEditable=false"
                   @keyup.enter="course.isCourseNameEditable=false;changeCourseAndRefreshCourseState(course)"
            />
          </td>
          <td class="table-cell">
            <div v-show="!course.isDayOfWeekEditable">
              <label
                  @click="onCourseClick(course, () => course.isDayOfWeekEditable = true)">
                {{ course.dayOfWeek }}
              </label>
            </div>
            <days-of-week-selector
                name="dayOfWeek"
                v-show="course.isDayOfWeekEditable"
                v-model="course.dayOfWeek"
                v-bind:init-course-name="course.courseName"
                v-bind:init-day-of-the-week="course.dayOfWeek"
                v-on:blur="course.isDayOfWeekEditable=false;"
                @dayOfTheWeekChanged="course.isDayOfWeekEditable=false;changeCourseAndRefreshCourseState(course)"
                @keyup.esc="course.isDayOfWeekEditable=false"
                @keyup.enter="course.isDayOfWeekEditable=false;changeCourseAndRefreshCourseState(course)"
            />
          </td>
          <td class="table-cell">
            <div v-show="!course.isTimeOfTheDayEditable">
              <label
                  @click="onCourseClick(course, () => course.isTimeOfTheDayEditable = true)">
                {{ course.timeOfTheDay }}
              </label>
            </div>
            <input v-show="course.isTimeOfTheDayEditable"
                   v-model="course.timeOfTheDay"
                   name="timeOfTheDay"
                   type="time"
                   @keydown.esc="course.isTimeOfTheDayEditable=false"
                   @keyup.enter="course.isTimeOfTheDayEditable=false;changeCourseAndRefreshCourseState(course)"
            />
          </td>
          <td>
            <div style="display: flex">
              <button
                  class="pause-button table-button"
                  v-on:click="pauseCourseRefresh(course)">
              </button>
            </div>
          </td>
          <td>
            <div style="display: flex">
              <button
                  class="delete-button"
                  :disabled="course.isPaused"
                  v-bind:class="{ 'table-button': !course.isPaused, 'inactive-table-button': course.isPaused}"
                  v-on:click="deleteCourseRefresh(course)">
              </button>
            </div>
          </td>
        </tr>
      </table>
    </div>
    <a href="https://aquabasilea.migrosfitnesscenter.ch/angebote/bewegung/kursprogramm" target="_blank">Aquabasilea
      kursprogramm</a>
  </div>
</template>
<script>
import WeeklyCoursesApi from '../mixins/WeeklyCoursesApi';
import DaysOfWeekSelector from "@/components/DaysOfWeekSelector";

export default {
  name: 'WeeklyCoursesOverview',
  components: {DaysOfWeekSelector},
  mixins: [WeeklyCoursesApi],
  computed: {
    weeklyCourses: function () {
      return this.$store.getters.weeklyCourses;
    },
    isLoading: function () {
      return this.$store.getters.isLoading;
    }
  },
  methods: {
    onCourseClick: function (course, setAttrEditableFunction) {
      if (!course.isPaused) {
        setAttrEditableFunction.apply(course);
      }
    },
    changeCourseAndRefreshCourseState: function (course) {
      this.changeCourse(course);
      this.$store.dispatch('setIsLoading', true);
      this.$emit('refreshCourseStateOverviewAndWeeklyCourses');// refresh since the order of the table-entries may have changed
    },
    deleteCourseRefresh: function (course) {
      this.$store.dispatch('setIsLoading', true);
      this.deleteCourse(course);
      if (course.isCurrentCourse) {
        this.$emit('refreshCourseStateOverviewAndWeeklyCourses');
      } else {
        this.$emit('refreshWeeklyCourses');
      }
    },
    pauseCourseRefresh: function (course) {
      this.$store.dispatch('setIsLoading', true);
      this.pauseResumeCourse(course);
      this.$emit('refreshWeeklyCourses');
    },
  },
  mounted() {
    this.fetchWeeklyCourses();
  }
}</script>

<style scoped>

.weekly-courses-overview {
  width: auto;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.table {
  width: auto;
  overflow-x: hidden;
  padding-bottom: 10px;
}

.table-cell {
  padding-left: 15px;
}

.isPaused {
  background: lightslategray;
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

</style>