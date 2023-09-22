<template>
  <div>
    <CAlert v-show="this.errorDetails" color="danger" class="error-details tile" style="justify-self: center">
      {{ this.errorDetails }}
    </CAlert>
  </div>
</template>
<script>
import '@coreui/coreui/dist/css/coreui.css';

export default {
  name: 'ErrorBox',
  data() {
    return {
      errorDetails: null
    }
  }
}
</script>
<style scoped>

.error-details {
  border-radius: 10px;
}
</style>