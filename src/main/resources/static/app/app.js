const AttributeBindingApp = {
  data() {
    return {
      messages: [],
      stompClient: null
    }
  },
  mounted() {
    this.initWsConnection()
  },
  methods: {
    initWsConnection() {
      var socket = new SockJS('/messages/recent');
          var stompClient = this.stompClient;
          stompClient = Stomp.over(socket);
          stompClient.connect({}, (frame)=> {
              console.log('Connected: ' + frame);
              stompClient.subscribe('/messages', (msg)=> {
              if (this.messages.length == 5) {
                this.messages.splice(4,1)
              }
              let body = JSON.parse(msg.body)
              this.messages.unshift({time:body.time, body:body.message})
              });
          });
    }
  }
}

Vue.createApp(AttributeBindingApp).mount('#app')