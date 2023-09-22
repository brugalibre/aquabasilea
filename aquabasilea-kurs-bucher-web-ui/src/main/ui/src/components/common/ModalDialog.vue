<template>
  <div class="modal-container">
    <div class="modal-body">
      <span class="modal-close" @click="this.$emit('cancel')">🗙</span>
      <h3>{{ title }}</h3>
      <label style="padding-bottom: 10px">{{ question }}</label>
      <div class="modal-action">
        <CButton color="info" size="sm" @click="this.$emit('confirm')" >{{ confirmTxt }} </CButton>
        <CButton color="info" size="sm" @click="this.$emit('cancel')">{{ cancelTxt }}</CButton>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'ModalDialog',
  props: ['title', 'question', 'confirmTxt', 'cancelTxt'],
}
</script>
<style>
.modal-container {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  width: 100%;
  height: 100%;
  background-color: #cececeb5;
}

.modal-body {
  background-color: #fff;
  border: 2px solid #74a2cf;
  border-radius: 10px;
  max-width: 300px;
  max-height: 215px;
  display: flex;
  flex-direction: column;
}

.modal-action {
  display: grid;
  grid-template-columns: 50% 50%;
  gap: 10px;
}

.modal-close {
  cursor: pointer;
  position: relative;
  align-self: end;
  right: -10px;
  top: -17px;
}
</style>