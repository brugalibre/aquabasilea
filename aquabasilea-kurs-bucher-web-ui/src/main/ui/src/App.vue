<template>
  <div id="app">
    <h1> {{ stagingMsg }} </h1>
    <loading v-model:active="isLoading"
             :is-full-page="true"/>
    <div class="content">
      <div class="content-left-side">
        <course-state-overview
            class="tile course-state-overview"
            :key="courseStateOverviewRefreshKey"
            @refreshCourseStateOverviewAndWeeklyCourses="refreshCourseStateOverviewAndWeeklyCourses()"
            @refreshCourseStateOverview="refreshCourseStateOverview()">
        </course-state-overview>
        <add-course
            class="tile"
            @refreshCourseStateOverviewAndWeeklyCourses="refreshCourseStateOverviewAndWeeklyCourses">
        </add-course>
      </div>
      <weekly-courses-overview
          class="tile"
          :key="weeklyCoursesRefreshKey"
          @refreshCourseStateOverviewAndWeeklyCourses="refreshCourseStateOverviewAndWeeklyCourses()"
          @refreshWeeklyCourses="refreshWeeklyCourses()">
      </weekly-courses-overview>
    </div>
  </div>
</template>

<script>
import Loading from 'vue-loading-overlay';
import 'vue-loading-overlay/dist/vue-loading.css';
import CourseStateOverview from "@/components/CourseStateOverview";
import WeeklyCoursesOverview from "@/components/WeeklyCoursesOverview";
import AddCourse from "@/components/AddCourse";

export default {
  name: 'App',
  components: {
    AddCourse,
    Loading,
    WeeklyCoursesOverview,
    CourseStateOverview
  },
  data() {
    return {
      applicationTitle: 'Aquabasilea-Kurs Bucher',
      stagingMsg: 'Aquabasilea-Kurs-Bucher Webapplikation',
      courseStateOverviewRefreshKey: 0,
      weeklyCoursesRefreshKey: 0,
    };
  },
  computed: {
    isLoading: function () {
      return this.$store.getters.isLoading;
    },
  },
  watch: {
    applicationTitle: {
      immediate: true,
      handler() {
        document.title = this.applicationTitle;
      }
    }
  },
  methods: {
    refreshCourseStateOverview: function () {
      this.$store.dispatch('setIsLoading', true);
      setTimeout(() => {
        this.courseStateOverviewRefreshKey += 1;
        }, 1000);
      console.log('courseStateOverview refreshed: ' + this.courseStateOverviewRefreshKey);
    },
    refreshWeeklyCourses: function () {
      this.$store.dispatch('setIsLoading', true);
      setTimeout(() => {
        this.weeklyCoursesRefreshKey += 1;
      }, 1000);
      console.log('weeklyCourses refreshed: ' + this.weeklyCoursesRefreshKey);
    },
    refreshCourseStateOverviewAndWeeklyCourses: function () {
      this.$store.dispatch('setIsLoading', true);
      setTimeout(() => {
        this.weeklyCoursesRefreshKey += 1;
        this.courseStateOverviewRefreshKey += 1;
      }, 1000);
      console.log('weeklyCourses & courseStateOverview refreshed: ' + this.weeklyCoursesRefreshKey + ', ' + this.courseStateOverviewRefreshKey);
    },
  }
}
</script>

<style>
* {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.content {
  display: flex;
  justify-content: center;
}

.content-left-side {
  max-width: 410px;
  min-width: 410px;
  display: flex;
  flex-direction: column;
  height: auto;
}

.course-state-overview {
  flex-grow: 2;
}

.tile {
  padding: 10px;
  margin: 10px;
  box-shadow: inset 0 3px 6px rgba(0, 0, 0, 0.16), 0 4px 6px rgba(0, 0, 0, 0.45);
  border-radius: 10px;
}

button {
  border-collapse: collapse;
  background-color: #0095c9;
  border-color: lightskyblue;
  color: white;
}

button:disabled {
  border-collapse: collapse;
  background-color: lightslategray;
  border-color: darkgray;
}

button:disabled {
  background-color: #9F9F9F;
}

table, th, td {
  color: black;
  table-layout: auto;
  border-collapse: collapse;
  text-align: left;
  padding: 3px;
  white-space: nowrap;
}

tr {
  border-bottom: #0095c9 thin solid;
}

tr:last-child {
  border-bottom: transparent;
}

th {
  color: white;
  padding: 0.5vw 1.5vh;
  background-color: #0095c9;
}

th:first-child {
  border-top-left-radius: 7px;
}

th:last-child {
  border-top-right-radius: 7px;
}

table {
  background-color: white;
  border-radius: 7px;
}

th {
  background-color: #0095c9;
}

h1, h2, h3, label {
  word-wrap: break-word;
}

h1, h2 {
  text-align: center;
}
</style>
