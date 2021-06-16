<template>

<v-data-table
    :headers="headers"
    :items="mypage"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'Mypage',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "roomId", value: "roomId" },
            { text: "roomName", value: "roomName" },
            { text: "guestName", value: "guestName" },
            { text: "bookingDate", value: "bookingDate" },
            { text: "address", value: "address" },
            { text: "bookId", value: "bookId" },
            { text: "payAmt", value: "payAmt" },
            { text: "payDate", value: "payDate" },
            { text: "status", value: "status" },
            { text: "host", value: "host" },
        ],
        mypage : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/mypages')

      this.mypage = temp.data._embedded.mypages;

    },
    methods: {
    }
  }
</script>

