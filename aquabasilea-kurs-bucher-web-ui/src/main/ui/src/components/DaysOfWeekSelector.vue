<template>
  <select
      style="min-width: 130px; max-width: 130px"
      id="dayOfWeekSelector"
      name="dayOfWeek"
      v-model="dayOfWeek">
    <option
        v-for="dayOfTheWeek in daysOfTheWeek" :key="dayOfTheWeek"
        v-bind:value="dayOfTheWeek"> {{ dayOfTheWeek }}
    </option>
  </select>
</template>
<script>
import weeklyCoursesApi from '../mixins/WeeklyCoursesApi';

export default {
  name: 'DaysOfWeekSelector',
  props: ['initCourseName', 'initDayOfTheWeek'],
  mixins: [weeklyCoursesApi],
  data() {
    return {
      courseName: this.initCourseName,
      dayOfWeek: this.initDayOfTheWeek,
      daysOfTheWeek: [],
    }
  },
  methods: {
    reset: function () {
      this.dayOfWeek = null;
    }
  },
  watch: {
    dayOfWeek: {
      handler: function (newDayOfTheWeek, oldDayOfTheWeek) {
        if (newDayOfTheWeek && newDayOfTheWeek !== oldDayOfTheWeek) {
          this.$emit('dayOfTheWeekChanged', newDayOfTheWeek);
        }
      },
    },
    initCourseName: {
      handler: function () {
        this.fetchDaysOfTheWeek4Course(this.initCourseName);
      },
    }
  },
  mounted() {
    this.fetchDaysOfTheWeek4Course(this.initCourseName);
  },
}
</script>
