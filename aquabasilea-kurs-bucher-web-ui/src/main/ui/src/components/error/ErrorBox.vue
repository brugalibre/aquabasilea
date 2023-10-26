<template>
  <div>
    <CAlert v-show="this.hasErrors()" color="danger" class="error-details tile"
            style="justify-self: center">
      <label v-for="errorDetail in this.internalErrorDetails" :key="errorDetail">{{ errorDetail }}</label>
    </CAlert>
  </div>
</template>
<script>
import '@coreui/coreui/dist/css/coreui.css';

export default {
  name: 'ErrorBox',
  data() {
    return {
      errorDetails: null,
      internalErrorDetails: [...new Set([])],
    }
  },
  methods: {
    hasErrors: function () {
      return this.internalErrorDetails?.length > 0;
    },
    addError: function (error) {
      if (error) {
        this.internalErrorDetails.push(error);
      }
    }
  },
  watch: {
    errorDetails: {
      immediate: true,
      handler: function (newError) {
        this.addError(newError);
      },
    }
  },
}
</script>
<style scoped>

.error-details {
  border-radius: 10px;
}
</style>