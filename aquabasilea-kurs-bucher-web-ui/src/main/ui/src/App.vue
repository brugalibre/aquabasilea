<template>
  <div id="app">
    <h1> {{ stagingMsg }} </h1>
    <loading v-model:active="isLoading"
             :is-full-page="true"/>
    <div>
      <div class="centered-flex">
        <div class="content-left-side">
          <course-booker-state-overview
              class="tile course-state-overview"
              :key="courseStateOverviewRefreshKey"
              @refreshCourseStateOverviewAndWeeklyCourses="refreshCourseStateOverviewAndWeeklyCourses()"
              @refreshCourseStateOverview="refreshCourseStateOverview()">
          </course-booker-state-overview>
          <add-course
              class="tile"
              :key="addCourseRefreshKey"
              @error-occurred="errorOccurred"
              @refreshAddCourse="refreshAddCourse()"
              @refreshCourseStateOverviewAndWeeklyCourses="refreshCourseStateOverviewAndWeeklyCourses">
          </add-course>
        </div>
        <weekly-courses-overview
            class="tile"
            :key="weeklyCoursesRefreshKey"
            @error-occurred="errorOccurred"
            @refreshCourseStateOverviewAndWeeklyCourses="refreshCourseStateOverviewAndWeeklyCourses()"
            @refreshWeeklyCourses="refreshWeeklyCourses()">
        </weekly-courses-overview>
      </div>
      <CAlert v-show="this.errorDetails" color="danger" class="error-details tile" style="justify-self: center">
        {{ this.errorDetails }}
      </CAlert>
    </div>
  </div>
</template>

<script>
import Loading from 'vue-loading-overlay';
import 'vue-loading-overlay/dist/vue-loading.css';
import CourseBookerStateOverview from "@/components/CourseBookerStateOverview";
import WeeklyCoursesOverview from "@/components/WeeklyCoursesOverview";
import AddCourse from "@/components/AddCourse";
import CommonAquabasileaRestApi from "@/mixins/CommonAquabasileaRestApi";

export default {
  name: 'App',
  mixins: [CommonAquabasileaRestApi],
  components: {
    AddCourse,
    Loading,
    WeeklyCoursesOverview,
    CourseBookerStateOverview
  },
  data() {
    return {
      errorDetails: '',
      applicationTitle: 'Aquabasilea-Kurs Bucher',
      stagingMsg: 'Aquabasilea-Kurs-Bucher Webapplikation',
      courseStateOverviewRefreshKey: 0,
      weeklyCoursesRefreshKey: 0,
      addCourseRefreshKey: 0,
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
    errorOccurred: function (error) {
      this.errorDetails = error;
      console.log('App.vue: errorOccurred : \'' + this.errorDetails + '\'');
    },
    refreshCourseStateOverviewAndWeeklyCourses: function () {
      this.$store.dispatch('setIsLoading', true);
      setTimeout(() => {
        this.weeklyCoursesRefreshKey += 1;
        this.courseStateOverviewRefreshKey += 1;
      }, 1000);
      console.log('weeklyCourses & courseStateOverview refreshed: ' + this.weeklyCoursesRefreshKey + ', ' + this.courseStateOverviewRefreshKey);
    },
    refreshAddCourse: function () {
      this.$store.dispatch('setIsLoading', true);
      setTimeout(() => {
        console.log('refreshAddCourse refreshed: ' + this.addCourseRefreshKey);
        this.addCourseRefreshKey += 1;
        this.$store.dispatch('setIsLoading', false);
      }, 2500);
    },
  },
  mounted() {
    this.errorDetails = null;
  }
}
</script>

<style>
* {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.centered-flex {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}

.content-left-side {
  max-width: 410px;
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

/**
Somehow the @coreui/coreui/dist/css/coreui.css styles override the table styles here - and somehow
I couldn't import the coreui-style scoped. So thats why we use !important here
*/

tr {
  border-bottom: #0095c9 thin solid !important;
}

tr:last-child {
  border-bottom: transparent !important;
}

th {
  color: white !important;
  padding: 0.5vw 1.5vh !important;
  background-color: #0095c9 !important;
}

th:first-child {
  border-top-left-radius: 7px !important;
}

th:last-child {
  border-top-right-radius: 7px !important;
}

h1, h2, h3, h4, label {
  word-wrap: anywhere;
}

h1, h2, h3 {
  text-align: center;
}

h5 {
  padding-top: 10px;
  word-wrap: anywhere;
}

button {
  white-space: normal;
  word-wrap: break-word;
}

.grid-container {
  display: grid;
  row-gap: 10px;
}

.grid-container-40-60 {
  display: grid;
  grid-template-columns: 40% 60%;
  column-gap: 10px;
  row-gap: 10px;
  padding-right: 10px;
}

.grid-container-60-40 {
  display: grid;
  grid-template-columns: 60% 40%;
  column-gap: 10px;
  row-gap: 10px;
  padding-right: 10px;
}

.error-details {
  border-radius: 10px;
}

</style>
